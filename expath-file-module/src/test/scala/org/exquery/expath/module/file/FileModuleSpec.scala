/**
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

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.{Files, Path, StandardOpenOption}

import cats.effect.IO
import fs2.io
import org.specs2.mutable._
import java.nio.charset.StandardCharsets.UTF_8

class FileModuleSpec extends Specification {

  "The writeAll function" should {

    "write from `offset=0` bytes" in {
      val testData = "123456789".getBytes
      val testFile = Files.createTempFile("write-from-offset", "bin")
      writeTestFile(testFile, testData)

      val fm = new FileModule {}

      val is: InputStream = new ByteArrayInputStream(testData)
      val writer = fm.writeAll[IO](testFile, 0)
      io
        .readInputStream(IO(is), 4096, true)
        .to(writer)
        .run
        .unsafeRunSync()

      // check that the file now has the data we just wrote from offset 2
      val actual = new String(Files.readAllBytes(testFile), UTF_8)
      val expected = "123456789"

      actual mustEqual expected
    }

    "write from `offset=2` bytes" in {
      val testData = "123456789".getBytes
      val testFile = Files.createTempFile("write-from-offset", "bin")
      writeTestFile(testFile, testData)

      val fm = new FileModule {}

      val is: InputStream = new ByteArrayInputStream(testData)
      val writer = fm.writeAll[IO](testFile, 2)
      io
        .readInputStream(IO(is), 4096, true)
        .to(writer)
        .run
        .unsafeRunSync()

      // check that the file now has the data we just wrote from offset 2
      val actual = new String(Files.readAllBytes(testFile), UTF_8)
      val expected = "12123456789"

      actual mustEqual expected
    }

    "write from `offset=9` bytes" in {
      val testData = "123456789".getBytes
      val testFile = Files.createTempFile("write-from-offset", "bin")
      writeTestFile(testFile, testData)

      val fm = new FileModule {}

      val is: InputStream = new ByteArrayInputStream(testData)
      val writer = fm.writeAll[IO](testFile, 9)
      io
        .readInputStream(IO(is), 4096, true)
        .to(writer)
        .run
        .unsafeRunSync()

      // check that the file now has the data we just wrote from offset 2
      val actual = new String(Files.readAllBytes(testFile), UTF_8)
      val expected = "123456789123456789"

      actual mustEqual expected
    }
  }

  def writeTestFile(path: Path, data: Array[Byte]) {
    val f = Files.newOutputStream(path, StandardOpenOption.CREATE)
    try {
      f.write(data)
    } finally {
      f.close()
    }
  }

}
