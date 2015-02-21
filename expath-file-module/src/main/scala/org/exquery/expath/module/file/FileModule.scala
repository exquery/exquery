/**
 * Copyright © 2015, Adam Retter
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

import java.io.{FileInputStream, BufferedInputStream, InputStream, RandomAccessFile, IOException, File}
import java.nio.charset.{UnsupportedCharsetException, IllegalCharsetNameException, Charset}

import scodec.bits.ByteVector

import scala.io.Codec
import scalax.file.{FileSystem, Path}
import scalaz._
import Scalaz._
import Maybe._
import scalaz.stream._
import scalaz.concurrent.Task



object FileModule {
  val NAMESPACE = "http://expath.org/ns/file"
  val PREFIX = "file"

  val DEFAULT_BUF_SIZE = 4096  // 4KB
  val DEFAULT_CHAR_ENCODING = "UTF-8"

  type AppendStreamFn[T] = (Process[Task, T]) => \/[FileModuleError, Unit]
}

/**
 * Abstract functions for the EXPath
 * File module
 */
trait FileModule {

  import FileModule._

  /**
   * Determine if the path on the filesystem exists
   *
   * @param path
   */
  def exists(path: String) : Boolean = existingPath(path).isRight

  /**
   * Determine if the path points to a directory on the filesystem
   *
   * @param path
   */
  def isDir(path: String) : Boolean = asPath(path).isDirectory

  /**
   * Determine if the path points to a file on the filesystem
   *
   * @param path
   */
  def isFile(path: String) : Boolean = asPath(path).isFile

  /**
   * Retrieve the last modified time of the file/directory
   * indicated by the path
   *
   * @param path
   */
  def lastModified(path: String) : \/[FileModuleError, Long] = existingPath(path).map(_.lastModified)

  /**
   * Determine the size of the file on the filesystem
   * if the path points to a directory then 0 is returned
   *
   * @param path
   */
  def fileSize(file: String) : \/[FileModuleError, Long] = {
    existingPath(file).map {
      p =>
        if(p.isDirectory) {
          Some(0l)
        } else {
          p.size
        }
    }.flatMap {
      case Some(size) => size.right
      case None => FileModuleErrors.IoError.left
    }
  }

  /**
   * Write binary data to a file
   *
   * @param file
   * @param append determines whether to append to an existing file or overwrite a file
   */
  def write(file : String, append: Boolean) : \/[FileModuleError, AppendStreamFn[ByteVector]] = {
    val path = Path.fromString(file)
    val ep : \/[FileModuleError, Path] =
      if(!path.parent.map(_.exists).getOrElse(false)) {
        FileModuleErrors.NoDir.left
      } else if(path.isDirectory) {
        FileModuleErrors.IsDir.left
      } else {
        path.right
      }

    ep.map {
      path =>
        source: Process[Task, ByteVector] =>
          import Process._
          source.to(io.fileChunkW(path.toAbsolute.path, append = append)).run.attemptRun.leftMap(FileModuleErrors.IoError)
    }
  }

  /**
   * Write text data to a file
   *
   * @param file
   * @param append determines whether to append to an existing file or overwrite a file
   * @param encoding
   */
  def write(file : String, append: Boolean, encoding: String = DEFAULT_CHAR_ENCODING) : \/[FileModuleError, AppendStreamFn[String]] = {

    def encodeString(charset: Charset)(string: String) : ByteVector = ByteVector.view(string.getBytes(charset))

    charset(encoding).map {
      cs =>
        source: Process[Task, String] =>
          write(file, append = append).map(_(source.pipe(process1.lift(encodeString(cs)))))
    }
  }

  /**
   * Copies a file or directory (recursively)
   *
   * @param source the source to copy
   * @param target the destination for the copy
   */
  def copy(source: String, target: String): Maybe[FileModuleError] = {
    existingPath(source).flatMap {
      src =>
        val trg = Path.fromString(target)
        if (src.isDirectory && trg.exists && trg.isFile) {
          FileModuleErrors.Exists.left
        } else if (src.isFile && trg.exists && trg.isDirectory && (trg / src.name).isDirectory) {
          FileModuleErrors.IsDir.left
        } else {
          try {
            src.copyTo(trg, createParents = true, replaceExisting = true)
            Unit.right
          } catch {
            case e: Exception =>
              FileModuleErrors.IoError(e).left
          }
        }
    }.swap.toMaybe
  }

  /**
   * Creates a directory and any missing parent directories
   *
   * @param dir
   */
  def createDir(dir: String) : Maybe[FileModuleError] = {
    val path = Path.fromString(dir)
    if (path.exists) {
      if (path.isFile) {
        just(FileModuleErrors.Exists)
      } else {
        empty
      }
    } else {
      path.createDirectory(createParents = true)
      empty
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
   */
  def createTempDir(prefix: String, suffix: String, dir: Maybe[String]) : \/[FileModuleError, String] = {
    val mkdirF = targetDir(dir).map(_.map {
      p => (prefix: String, suffix: String) => Path.createTempDirectory(prefix, suffix, p.path)
    }) | ((prefix: String, suffix: String) => Path.createTempDirectory(prefix, suffix)).right

    try {
      mkdirF.map(_(prefix, suffix).path)
    } catch {
      case e: Exception =>
        FileModuleErrors.IoError(e).left
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
  def createTempFile(prefix: String, suffix: String, dir: Maybe[String]) = {
    val createTempFileF = targetDir(dir).map(_.map {
      p => (prefix: String, suffix: String) => Path.createTempFile(prefix, suffix, p.path)
    }) | ((prefix: String, suffix: String) => Path.createTempFile(prefix, suffix)).right

    try {
      createTempFileF.map(_(prefix, suffix).path)
    } catch {
      case e: Exception =>
        FileModuleErrors.IoError(e).left
    }
  }

  /**
   * Deletes a file or directory from the filesystem
   *
   * @param path
   * @param recursive required when deleting a non-empty directory
   */
  def delete(path: String, recursive: Boolean = false): Maybe[FileModuleError] = {
    existingPath(path).flatMap {
      p =>
        try {
          if (recursive) {
            p.deleteRecursively(true)
          } else {
            p.delete(true)
          }
          Unit.right
        } catch {
          case e: Exception =>
            FileModuleErrors.IoError(e).left
        }
    }.swap.toMaybe
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
  def list(dir: String, recursive: Boolean = false, pattern: Maybe[String]) : \/[FileModuleError, Process[Task, String]] = ls(dir, recursive, pattern, relative = true)

  private def ls(dir: String, recursive: Boolean = false, pattern: Maybe[String], relative : Boolean = false) : \/[FileModuleError, Process[Task, String]]  = {
    existingPath(dir).leftMap(_ => FileModuleErrors.NoDir).map {
      p =>
        (p,
        if(recursive) {
          pattern.map(p.children(_)) | p.children()
        } else {
          pattern.map(p.descendants(_)) | p.descendants()
        })
    }.map {
      case(p, ps) =>
        io.resource(Task.delay(ps))(ps => Task.delay()) {
          ps =>
            lazy val it = ps.iterator
            Task.delay {
              if(it.hasNext)
                it.next.relativize(p).path
              else
                throw Cause.Terminated(Cause.End)
            }
        }
    }
  }

  /**
   * Moves a file or directory
   *
   * @param source the source to move
   * @param target the destination for the move
   */
  def move(source: String, target: String): Maybe[FileModuleError] = {
    existingPath(source).flatMap {
      src =>
        val trg = Path.fromString(target)
        if (src.isDirectory && trg.exists && trg.isFile) {
          FileModuleErrors.Exists.left
        } else if (trg.exists && trg.isDirectory && (trg / src.name).isDirectory) {
          FileModuleErrors.IsDir.left
        } else {
          try {
            src.moveTo(trg, replace = true, atomicMove = true)
            Unit.right
          } catch {
            case e: Exception =>
              FileModuleErrors.IoError(e).left
          }
        }
    }.swap.toMaybe
  }

  /**
   * Reads binary data from a file
   *
   * @param file
   * @param offset Optionally the offset to start reading from. Starts from 0 otherwise.
   * @param length Optionally the length of data to read. If unspecified then all data is read.
   */
  def readBinary(file: String, offset: Int = 0, length: Maybe[Int] = empty) : \/[FileModuleError, Channel[Task, Int, ByteVector]] = {
    existingPath(file).flatMap {
      p =>
        if(p.isDirectory) {
          FileModuleErrors.IsDir.left
        } else if(offset < 0 || length.getOrElse(0) < 0) {
          FileModuleErrors.OutOfRange.left
        } else {
          fileChunkRRange(p.toAbsolute.path).right
        }
    }
  }

  /**
   * Read text line by line from a file
   *
   * @param file
   * @param encoding Optionally the character encoding to use, otherwise UTF-8 will be used.
   */
  def readText(file: String, encoding: String = DEFAULT_CHAR_ENCODING) : \/[FileModuleError, Process[Task, String]] = {
    existingPath(file).flatMap {
      p =>
        if(p.isDirectory) {
          FileModuleErrors.IsDir.left
        } else {
          charset(encoding).map {
            cs =>
              io.linesR(p.toAbsolute.path)(Codec(cs))
          }
        }
    }
  }

  /**
   * Writes binary data to a file
   *
   * @param file
   * @param offset If the file exists, then start writing the data at offset. Starts from 0 otherwise.
   */
  def writeBinary(file: String, offset: Int = 0) : \/[FileModuleError, AppendStreamFn[ByteVector]] = {
    val path = Path.fromString(file)
    val ep : \/[FileModuleError, Path] =
      if(!path.parent.map(_.exists).getOrElse(false)) {
        FileModuleErrors.NoDir.left
      } else if(path.isDirectory) {
        FileModuleErrors.IsDir.left
      } else if(offset < 0 || path.size.map(sz => offset > sz).getOrElse(false)) {
        FileModuleErrors.OutOfRange.left
      } else {
        path.right
      }

    ep.map {
      path =>
        source: Process[Task, ByteVector] =>
          import Process._
          source.to(fileChunkWOff(path.toAbsolute.path, offset)).run.attemptRun.leftMap(FileModuleErrors.IoError)
    }
  }

  /**
   * Get the name of a file/directory indicated by the path
   *
   * @param path
   */
  def name(path: String) = asPath(path).name

  /**
   * Get the parent path of a file/directory indicated by the path
   *
   * @param path
   */
  def parent(path: String) = asPath(path).parent.toMaybe.map(_.toAbsolute.path)

  /**
   * Get the immediate children of a directory indicated by the path
   *
   * @param path
   * 
   * @return A process that produces absolute paths
   */
  def children(path: String) : \/[FileModuleError, Process[Task, String]] = ls(path, recursive = false, empty, relative = false)

  /**
   * Convert a path into the native representation used by the
   * operating system.
   *
   * @param path
   */
  def pathToNative(path: String): \/[FileModuleError, String] = try {
    asPath(path).toAbsolute.path.right
  } catch {
    case e: IOException =>
      FileModuleErrors.IoError(e).left
  }

  /**
   * Creates a URI representation of the path
   *
   * @param path
   */
  def pathToUri(path: String) = asPath(path).toURI

  /**
   * Resolves a path relative to the current working directory
   *
   * @param path
   */
  def resolvePath(path: String) = asPath(path).toAbsolute.path

  /**
   * The directory separator used by the operating system.
   */
  lazy val dirSeparator : String = FileSystem.default.separator

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
  lazy val tempDir : String = sys.props("java.io.tmpdir")

  /**
   * The path to the current working directory of where the operating
   * process is executing.
   */
  lazy val currentDir : String = sys.props("user.dir")

  /**
   * Looks up a character set by name
   *
   * @param name The name of the character set
   *
   * @return Either UnknownEncoding error or a Charset object
   */
  private def charset(name: String) : \/[FileModuleError, Charset] = try {
    Charset.forName(name).right
  } catch {
    case e @ (_: IllegalCharsetNameException | _: UnsupportedCharsetException | _: IllegalArgumentException) =>
      FileModuleErrors.UnknownEncoding(e).left
  }

  /**
   * Creates a process sink for writing to a file
   * at an offset in chunks
   *
   * @param f The path to the file
   * @param offset The offset to start writing at, defaults to 0
   *
   * @return A sink for writing to the file
   */
  private def fileChunkWOff(f: String, offset: Int = 0) : Sink[Task, ByteVector] = {
    io.resource(Task.delay {
      val raf = new RandomAccessFile(f, "rwd")
      if(offset > 0) {
        try {
          raf.seek(offset)
        } catch {
          case ioe: IOException =>
            throw Cause.Terminated(Cause.Error(ioe))
        }
      }
      raf
    })(raf => Task.delay(raf.close)) {
      raf =>
        Task.now((bytes: ByteVector) => Task.delay(raf.write(bytes.toArray)))
    }
  }

  /**
   * Returns a Channel that can read the bytes in the range `offset -> offset+length`
   * from a file in chunks
   *
   * @param f The path to the file
   * @param bufferSize The size of the chunks to read. Note the last chunk read may be smaller than the buffer size
   * @param offset The offset to start reading from, defaults to 0
   * @param length Optionally the length to read, if the length is not specified we assume EOF
   *
   * @return A channel for reading the range from the file
   */
  private def fileChunkRRange(f: String, bufferSize: Int = DEFAULT_BUF_SIZE, offset: Int = 0, length: Maybe[Int] = empty) : Channel[Task, Int, ByteVector] = chunkRRange(new FileInputStream(f), bufferSize, offset, length)

  /**
   * Returns a Channel that can read the bytes in the range `offset -> offset+length`
   * from an InputStream in chunks
   *
   * @param is The InputStream
   * @param bufferSize The size of the chunks to read. Note the last chunk read may be smaller than the buffer size
   * @param offset The offset to start reading from, defaults to 0
   * @param length Optionally the length to read, if the length is not specified we assume EOF
   *
   * @return A channel for reading the range from the InputStream
   */
  private[file] def chunkRRange(is: InputStream, bufferSize: Int = DEFAULT_BUF_SIZE, offset: Int = 0, length: Maybe[Int] = empty) : Channel[Task, Int, ByteVector] = {
    unsafeChunkRRange(new BufferedInputStream(is, bufferSize), offset, length).map(f => (n: Int) => {
      val buf = new Array[Byte](n)
      f(buf).map(ByteVector.view)
    })
  }

  /**
   * Returns a function that can read the bytes in the range `offset -> offset+length`
   * from an InputStream in chunks
   *
   * @param is A function which realises an InputStream
   * @param offset The offset to start reading from, defaults to 0
   * @param length Optionally the length to read, if the length is not specified we assume EOF
   *
   * @return A function which given a buffer can produce a Channel for reading the InputStream
   */
  private def unsafeChunkRRange(is: => InputStream, offset: Int = 0, length: Maybe[Int] = empty): Channel[Task,Array[Byte],Array[Byte]] = io.resource(Task.delay {
    if(offset > 0) {
      scala.util.Try(is.skip(offset)) match {
        case scala.util.Success(n) if (n < offset) =>
          throw Cause.Terminated(Cause.Error(new IndexOutOfBoundsException()))
        case scala.util.Failure(e) =>
          throw Cause.Terminated(Cause.Error(e))
        case _ =>
          is
      }
    } else {
      is
    }
  })(src => Task.delay(src.close)) {
    src =>
      var consumed = 0
      Task.now { (buf: Array[Byte]) => Task.delay {
        if(length.map(l => consumed >= l).getOrElse(false)) {
          throw Cause.Terminated(Cause.End)
        } else {
          val m = src.read(buf)
          if (m == -1) {
            throw Cause.Terminated(Cause.End)
          } else {
            val chunk = length match {
              case Just(l) if(consumed + m > l) =>
                val remaining = l - consumed;
                buf.take(remaining)

              case _ if(m == buf.length) =>
                buf

              case _ =>
                buf.take(m)

            }
            consumed += m
            chunk
          }
        }
      }}
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
  private def targetDir(dir: Maybe[String]) : Maybe[\/[FileModuleError, Path]] =
    dir.map(Path.fromString).map {
      case p if !p.exists =>
        FileModuleErrors.NoDir.left
      case p if(p.isFile) =>
        FileModuleErrors.NoDir.left
      case p =>
        p.right
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
  private def asPath(path: String) : Path = {
    if(path.startsWith("file:")) {
      Path(new java.io.File(new java.net.URI(path)))
    } else {
      Path.fromString(path)
    }
  }

  /**
   * Constructs a path object only it the path already exists on the filesystem
   *
   * @param path The filesystem path
   *
   * @return Either NotFound error or a Path object
   */
  private def existingPath(path: String) : \/[FileModuleError, Path] = {
    val p = asPath(path)
    if(!p.exists) {
      FileModuleErrors.NotFound.left
    } else {
      p.right
    }
  }
}

/**
 * Represents an Error caused by a process in the
 * EXPath File Module
 */
case class FileModuleError(name: String, description : String, exception: Maybe[Throwable] = empty)

/**
 * Errors that may be expressed by the EXPath File Module
 */
object FileModuleErrors {
  val NotFound = FileModuleError("not-found", "The specified path does not exist")
  val Exists = FileModuleError("exists", "The specified path already exists")
  val NoDir = FileModuleError("no-dir", "The specified path does not point to a directory")
  val IsDir = FileModuleError("is-dir", "The specified path points to a directory")
  private def UnknownEncoding(exception: Maybe[Throwable]) = FileModuleError("unknown-encoding", "The specified encoding is not supported", exception)
  val UnknownEncoding : FileModuleError = UnknownEncoding(empty[Throwable])
  def UnknownEncoding(exception: Throwable) : FileModuleError = UnknownEncoding(just(exception))
  val OutOfRange = FileModuleError("out-of-range", "The specified offset or length is negative, or the chosen values would exceed the file bounds")
  private def IoError(exception: Maybe[Throwable]) = FileModuleError("A generic file system error occurred", "A generic file system error occurred", exception)
  val IoError : FileModuleError = IoError(empty[Throwable])
  def IoError(exception: Throwable) : FileModuleError = IoError(just(exception))
}
