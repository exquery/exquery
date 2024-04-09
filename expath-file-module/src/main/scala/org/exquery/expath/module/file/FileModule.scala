/*
 * Copyright © 2012, Adam Retter / EXQuery
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.expath.module.file

import java.io._
import java.net.URI
import java.nio.file._
import java.nio.charset.{Charset, IllegalCharsetNameException, StandardCharsets, UnsupportedCharsetException}
import java.util.Comparator

import cats.effect.{IO, Sync}
import fs2.io.file.{FileHandle, pulls}
import fs2.text.utf8DecodeC
import fs2.{Chunk, Pipe, Pull, Sink, Stream, io}

import scala.util.Try


object FileModule {
  val NAMESPACE = "http://expath.org/ns/file"
  val PREFIX = "file"

  val DEFAULT_BUF_SIZE = 4096  // 4KB
  val DEFAULT_CHAR_ENCODING = "UTF-8"
}

/**
 * Abstract implementation of functions for the EXPath
 * File module {@see http://expath.org/spec/file}
 */
trait FileModule {

  import FileModule._

  /**
    * Determine if the path on the filesystem exists
    *
    * @param path
    */
  def exists[F[_]](path: String)(implicit F: Sync[F]): F[Boolean] = F.map(existingPath(path))(_ => true)

  /**
    * Determine if the path points to a directory on the filesystem
    *
    * @param path
    */
  def isDir[F[_]](path: String)(implicit F: Sync[F]): F[Boolean] = F.map(asPath(path))(p => Files.isDirectory(p))

  /**
    * Determine if the path points to a file on the filesystem
    *
    * @param path
    */
  def isFile[F[_]](path: String)(implicit F: Sync[F]): F[Boolean] = F.map(asPath(path))(p => Files.isRegularFile(p))

  /**
    * Retrieve the last modified time of the file/directory
    * indicated by the path
    *
    * @param path
    */
  def lastModified[F[_]](path: String)(implicit F: Sync[F]): F[Long] = F.map(existingPath(path))(p => Files.getLastModifiedTime(p).toMillis)

  /**
    * Determine the size of the file on the filesystem
    * if the path points to a directory then 0 is returned
    *
    * @param file
    */
  def fileSize[F[_]](file: String)(implicit F: Sync[F]): F[Long] = {
    F.map(existingPath(file)) { path =>
      if (Files.isDirectory(path)) {
        0l
      } else {
        Try(Files.size(path)).getOrElse(0l)
      }
    }
  }

  /**
    * Write binary data to a file
    *
    * @param file
    * @param append determines whether to append to an existing file or overwrite a file
    */
  def writeBinary[F[_]](file: String, append: Boolean)(implicit F: Sync[F]): F[Sink[F, Byte]] = {
    F.map(asPath(file))(path =>
      if (!Option(path.getParent).map(Files.exists(_)).getOrElse(false)) {
        throw new IOException("NoDir")
      } else if (Files.isDirectory(path)) {
        throw new IOException("IsDir")
      } else {
        if (append) {
          io.file.writeAll(path, Seq(StandardOpenOption.APPEND))
        } else {
          io.file.writeAll(path)
        }
      }
    )
  }

  /**
    * Write text data to a file
    *
    * @param file
    * @param append determines whether to append to an existing file or overwrite a file
    * @param encoding
    */
  def writeText[F[_]](file: String, append: Boolean, encoding: String = DEFAULT_CHAR_ENCODING)(implicit F: Sync[F]): F[Sink[F, String]] = {
    F.map(F.product(charset(encoding), writeBinary(file, append))) { case (charset, writer) =>
      encoder(charset) andThen writer
    }
  }

  /**
    * Writes binary data to a file
    *
    * @param file
    * @param offset If the file exists, then start writing the data at offset. Starts from 0 otherwise.
    */
  def writeBinary[F[_]](file: String, offset: Int = 0)(implicit F: Sync[F]): F[Sink[F, Byte]] = {
    F.map(asPath(file))(path =>
      if (!Option(path.getParent).map(Files.exists(_)).getOrElse(false)) {
        throw new FileModuleException(FileModuleErrors.NoDir)
      } else if (Files.isDirectory(path)) {
        throw new FileModuleException(FileModuleErrors.IsDir)
      } else if (offset < 0 || offset > Files.size(path)) {
        throw new FileModuleException(FileModuleErrors.OutOfRange)
      } else {
        writeAll(path, offset)
      }
    )
  }

  /**
   * Similar to fs2.io.file#writeAll(Path, Seq) but
   * starts writing to the file at {@code offset}
   */
  private[file] def writeAll[F[_]](path: Path, offset: Int, flags: Seq[StandardOpenOption] = List(StandardOpenOption.CREATE))(implicit F: Sync[F]): Sink[F, Byte] = {

    def _writeAllToFileHandle1[F[_]](in: Stream[F, Byte], out: FileHandle[F], offset: Long): Pull[F, Nothing, Unit] =
      in.pull.unconsChunk.flatMap {
        case None => Pull.done
        case Some((hd,tl)) =>
          _writeAllToFileHandle2(hd, out, offset) >> _writeAllToFileHandle1(tl, out, offset + hd.size)
      }

    def _writeAllToFileHandle2[F[_]](buf: Chunk[Byte], out: FileHandle[F], offset: Long): Pull[F, Nothing, Unit] =
      Pull.eval(out.write(buf, offset)) flatMap { (written: Int) =>
        if (written >= buf.size)
          Pull.pure(())
        else
          _writeAllToFileHandle2(buf.drop(written).toOption.get.toChunk, out, offset + written)
      }

    in => (for {
      out <- pulls.fromPath(path, StandardOpenOption.WRITE :: flags.toList)
      _ <- _writeAllToFileHandle1(in, out.resource, offset)
    } yield ()).stream
  }

  /**
    * Reads binary data from a file
    *
    * @param file
    * @param offset Optionally the offset to start reading from. Starts from 0 otherwise.
    * @param length Optionally the length of data to read. If unspecified then all data is read.
    */
  def readBinary[F[_]](file: String, offset: Int = 0, length: Option[Int] = None)(implicit F: Sync[F]) : F[Stream[IO, Byte]] = {
    F.map(existingPath(file)) { path =>
      if(Files.isDirectory(path)) {
        throw new FileModuleException(FileModuleErrors.IsDir)
      } else if(offset < 0 || length.getOrElse(0) < 0) {
        throw new FileModuleException(FileModuleErrors.OutOfRange)
      } else {

        val is: InputStream = Files.newInputStream(path)
        val stream : Stream[IO, Byte] = io.readInputStream(IO(is), DEFAULT_BUF_SIZE, true)

        // move to the offset
        val offsetStream = stream.drop(offset)

        // restrict to length
        length match {
          case Some(len) =>
            offsetStream.take(len)
          case None =>
            offsetStream
        }
      }
    }
  }

  /**
    * Read text line by line from a file
    *
    * @param file
    * @param encoding Optionally the character encoding to use, otherwise UTF-8 will be used.
    */
  def readText[F[_]](file: String, encoding: String = DEFAULT_CHAR_ENCODING)(implicit F: Sync[F]) : F[Stream[IO, String]] = {
    F.map(F.product(charset(encoding), readBinary(file, 0, None))) { case (charset, reader) =>
      reader through decoder(charset)
    }
  }

  private def encoder[F[_]](charset: Charset): Pipe[F, String, Byte] = _.flatMap(s => Stream.chunk(Chunk.bytes(s.getBytes(charset))))

  private def decoder[F[_]](charset: Charset): Pipe[F, Byte, String] = {
    if(charset == StandardCharsets.UTF_8) {
      _.chunks.through(utf8DecodeC)
    } else {
      //TODO(AR) we need to add support for charsets other than UTF-8
      throw new FileModuleException(FileModuleErrors.UnknownEncoding)
    }
  }

  /**
   * Copies a file or directory (recursively)
   *
   * @param source the source to copy
   * @param target the destination for the copy
   */
  def copy[F[_]](source: String, target: String)(implicit F: Sync[F]): F[String] = {
    F.map(F.product(existingPath(source), asPath(target))) { case (src, trg) =>

      if (Files.isDirectory(src) && Files.exists(trg) && Files.isRegularFile(trg)) {
        throw new FileModuleException(FileModuleErrors.Exists)
      } else if (Files.isRegularFile(src) && Files.exists(trg) && Files.isDirectory(trg) && Files.isDirectory(trg.resolve(src.getFileName))) {
        throw new FileModuleException(FileModuleErrors.IsDir)
      } else {
        try {
          val dest = Files.copy(src, trg, StandardCopyOption.REPLACE_EXISTING)
          dest.toAbsolutePath.toString
        } catch {
          case e: Exception =>
            throw new FileModuleException(FileModuleErrors.IoError, e)
        }
      }
    }
  }

  /**
   * Creates a directory and any missing parent directories
   *
   * @param dir
   */
  def createDir[F[_]](dir: String)(implicit F: Sync[F]) : F[String] = {
    F.map(asPath(dir)) { path =>
      if (Files.exists(path)) {
        if (Files.isRegularFile(path)) {
          throw new FileModuleException(FileModuleErrors.Exists)
        }
        path.toAbsolutePath.toString
      } else {
        val newDir = Files.createDirectories(path)
        newDir.toAbsolutePath.toString
      }
    }
  }

  /**
   * Create a temporary directory
   *
   * @param prefix The prefix for the name of the temporary directory
   * @param suffix The suffix for the name of the temporary directory
   * @param dir Optionally an existing directory in which to create the
   *            new temporary directory. If not specified then the JVM
   *            default temporary directory is used
   *
   * @return Either an error or the full path to the created directory
   */
  def createTempDir[F[_]](prefix: String, suffix: String, dir: Option[String])(implicit F: Sync[F]) : F[String] = {
    targetDir(dir) match {
      case Some(fDir) =>
        F.map(fDir)(trg => pathResult(Files.createTempDirectory(trg, prefix)))  //TODO(AR) what about the suffix?
      case None =>
        F.delay {
          pathResult(Files.createTempDirectory(prefix)) //TODO(AR) what about the suffix?
        }
    }
  }

  /**
   * Create a temporary file
   *
   * @param prefix The prefix for the name of the file directory
   * @param suffix The suffix for the name of the file directory
   * @param dir Optionally an existing directory in which to create the
   *            new temporary file. If not specified then the JVM
   *            default temporary directory is used
   */
  def createTempFile[F[_]](prefix: String, suffix: String, dir: Option[String])(implicit F: Sync[F]) : F[String] = {
    targetDir(dir) match {
      case Some(fDir) =>
        F.map(fDir)(trg => pathResult(Files.createTempFile(trg, prefix, suffix)))
      case None =>
        F.delay {
          pathResult(Files.createTempFile(prefix, suffix))
        }
    }
  }

  /**
   * Deletes a file or directory from the filesystem
   *
   * @param path
   * @param recursive required when deleting a non-empty directory
   */
  def delete[F[_]](path: String, recursive: Boolean = false)(implicit F: Sync[F]): F[Unit] = {
    F.flatMap(existingPath(path)) { path =>
      if (recursive) {
        deleteRecursively(path)
      } else {
        F.delay { Files.delete(path) }
      }
    }
  }

  private def deleteRecursively[F[_]](p: Path)(implicit F: Sync[F]) : F[Unit] = {
    F.delay {
      val stream = Files.walk(p)
      try {
        stream
          .sorted(Comparator.reverseOrder())
          .forEach(Files.delete(_))
      } finally {
        stream.close()
      }
    }
  }

  /**
   * List the files and directories in a directory
   *
   * @param dir
   * @param recursive Whether we should list files and directories from all descendant directories too
   * @param pattern An optional glob pattern for filtering the returned file/directory names
   *
   * @return A process that produces relative paths
   */
  def list[F[_]](dir: String, recursive: Boolean = false, pattern: Option[String])(implicit F: Sync[F]) : F[Seq[String]] = ls(dir, recursive, pattern, relative = true)

  private def ls[F[_]](dir: String, recursive: Boolean = false, pattern: Option[String], relative : Boolean = false)(implicit F: Sync[F]) : F[Seq[String]]  = {

    //TODO(AR) do we need the relative flag?

    def descendants(path: Path, pattern: Option[String]): Seq[String] = {
      //TODO(AR) implement pattern support
      import scala.compat.java8.StreamConverters._
      val stream = Files.walk(path)
      try {
        return stream.toScala[Seq].map(pathResult)
      } finally {
        stream.close()
      }
    }

    def children(path: Path, pattern: Option[String]): Seq[String] = {
      //TODO(AR) implement pattern support
      import scala.compat.java8.StreamConverters._
      val stream = Files.list(path)
      try {
        return stream.toScala[Seq].map(pathResult)
      } finally {
        stream.close()
      }
    }

    F.map(
      F.adaptError(existingPath(dir)) {
      case t: FileModuleException =>
        new FileModuleException(FileModuleErrors.NoDir, t)
      }
    ) { path =>
      if(recursive) {
        descendants(path, pattern)
      } else {
        children(path, pattern)
      }
    }
  }

  /**
   * Moves a file or directory
   *
   * @param source the source to move
   * @param target the destination for the move
   */
  def move[F[_]](source: String, target: String)(implicit F: Sync[F]): F[String] = {
    F.map(F.product(existingPath(source), asPath(target))) { case (src, trg) =>

      if (Files.isDirectory(src) && Files.exists(trg) && Files.isRegularFile(trg)) {
        throw new FileModuleException(FileModuleErrors.Exists)
      } else if (Files.exists(trg) && Files.isDirectory(trg) && Files.isDirectory(trg.resolve(src.getFileName))) {
        throw new FileModuleException(FileModuleErrors.IsDir)
      } else {
        try {
          val dest = Files.move(src, trg, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
          dest.toAbsolutePath.toString
        } catch {
          case e: Exception =>
            throw new FileModuleException(FileModuleErrors.IoError, e)
        }
      }
    }
  }

  /**
   * Get the name of a file/directory indicated by the path
   *
   * @param path
   */
  def name[F[_]](path: String)(implicit F: Sync[F]): F[String] = F.map(asPath(path))(_.getFileName.toString)

  /**
   * Get the parent path of a file/directory indicated by the path
   *
   * @param path
   */
  def parent[F[_]](path: String)(implicit F: Sync[F]): F[Option[String]] = F.map(asPath(path))(p => Option(p.getParent).map(_.toAbsolutePath + dirSeparator))

  // /**
  //  * Get the immediate children of a directory indicated by the path
  //  *
  //  * @param path
  //  * 
  //  * @return A process that produces absolute paths
  //  */
  // def children(path: String) : \/[FileModuleError, Process[Task, String]] = ls(path, recursive = false, empty, relative = false)

  /**
   * Convert a path into the native representation used by the
   * operating system.
   *
   * @param path
   */
  def pathToNative[F[_]](path: String)(implicit F: Sync[F]): F[String] = F.map(asPath(path))(_.toAbsolutePath.toString)

  /**
   * Creates a URI representation of the path
   *
   * @param path
   */
  def pathToUri[F[_]](path: String)(implicit F: Sync[F]): F[URI] = F.map(asPath(path))(_.toUri)

  /**
   * Resolves a path relative to the current working directory
   *
   * @param path
   */
  def resolvePath[F[_]](path: String)(implicit F: Sync[F]): F[String] = F.map(asPath(path))(p => pathResult(p.toAbsolutePath))

  /**
   * The directory separator used by the operating system.
   */
  lazy val dirSeparator : String = FileSystems.getDefault.getSeparator

  /**
   * The line separator used by the operating system.
   */
  lazy val lineSeparator : String = sys.props("line.separator")

  /**
   * The path separator used by the operating system.
   */
  lazy val pathSeparator : String = File.pathSeparator

  /**
   * The path to the temporary directory used by the JVM on this system.
   */
  lazy val tempDir : String = sys.props("java.io.tmpdir") + dirSeparator

  /**
   * The path to the current working directory of where the operating
   * process is executing.
   */
  lazy val currentDir : String = sys.props("user.dir") + dirSeparator

  /**
   * Looks up a character set by name
   *
   * @param name The name of the character set
   *
   * @return Either UnknownEncoding error or a Charset object
   */
  private def charset[F[_]](name: String)(implicit F: Sync[F]) : F[Charset] = {
    F.delay {
      try {
        Charset.forName(name)
      } catch {
        case e@(_: IllegalCharsetNameException | _: UnsupportedCharsetException | _: IllegalArgumentException) =>
          throw new FileModuleException(FileModuleErrors.UnknownEncoding, e)
      }
    }
  }

  /**
   * Constructs a path object for a target directory, only if:
   *
   * 1) If the target directory exists on the filesystem
   * 2) If the target directory is not a file
   *
   * @param dir The path to the potential target directory
   *
   * @return NoDir error or a Path object
   */
  private def targetDir[F[_]](dir: Option[String])(implicit F: Sync[F]) : Option[F[Path]] = {
    dir.map(asPath(_)).map { path =>
      F.flatMap(path){ p =>
        F.delay {
          if (!Files.exists(p)) {
            throw new FileModuleException(FileModuleErrors.NoDir)
          } else if (Files.isRegularFile(p)) {
            throw new FileModuleException(FileModuleErrors.NoDir)
          } else {
            p
          }
        }
      }
    }
  }

  /**
   * Constructs a Path object from a `path`
   * as defined by the EXPath File Module spec
   *
   * @param path A path may either:
   *   1) An absolute or relative UNIX/Linux path
   *   2) An absolute or relative Windows path
   *   3) An absolute file URI
   *
   * @return A Path object
   */
  private def asPath[F[_]](path: String)(implicit F: Sync[F]) : F[Path] = {
    F.delay {
      Paths.get(path)
    }
  }

  /**
   * Constructs a path object only it the path already exists on the filesystem
   *
   * @param path The filesystem path
   *
   * @return Either NotFound error or a Path object
   */
  private def existingPath[F[_]](path: String)(implicit F: Sync[F]) : F[Path] = {
    F.flatMap(asPath(path)) { p =>
      F.delay {
        if (!Files.exists(p)) {
          throw new FileModuleException(FileModuleErrors.NotFound)
        } else {
          p
        }
      }
    }
  }

  /**
   * Paths returned by the EXPath File Module
   * must be suffixed with the system directory
   * separator according to the spec; Seems dumb
   * to me but hey-ho!
   *
   * @param p
   * @return a path as defined by the EXPath
   *   File Module spec
   */
  private def pathResult(p: Path) : String = {
    if(Files.isDirectory(p)) {
      p.toString + dirSeparator
    } else {
      p.toString
    }
  }
}

/**
 * Exception class for signaling errors from functions that return {@code F}
 */
case class FileModuleException(fileModuleError: FileModuleError, cause: Throwable) extends Exception(fileModuleError.description, cause) {
  def this(fileModuleError: FileModuleError) {
    this(fileModuleError, null)
  }
}

/**
 * Represents an Error caused by a process in the
 * EXPath File Module
 */
case class FileModuleError(name: String, description : String)

/**
 * Errors that may be expressed by the EXPath File Module
 */
object FileModuleErrors {
  val NotFound = FileModuleError("not-found", "The specified path does not exist")
  val Exists = FileModuleError("exists", "The specified path already exists")
  val NoDir = FileModuleError("no-dir", "The specified path does not point to a directory")
  val IsDir = FileModuleError("is-dir", "The specified path points to a directory")
  val UnknownEncoding = FileModuleError("unknown-encoding", "The specified encoding is not supported")
  val OutOfRange = FileModuleError("out-of-range", "The specified offset or length is negative, or the chosen values would exceed the file bounds")
  val IoError = FileModuleError("A generic file system error occurred", "A generic file system error occurred")
}
