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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.specs2.mutable._
import scalaz._
import Scalaz._
import scalaz.stream._
import Maybe._

class FileModuleSpec extends Specification {

  "The chunkRRange function" should {

    "read only `length` bytes" in {
      val testData = new ByteArrayInputStream("123456789".getBytes)
      val fm = new FileModule{}
      val in = fm.chunkRRange(testData, 4096, 0, just(5))
      val outputBuf = new ByteArrayOutputStream()

      Process.constant(4096).toSource.through(in).to(io.chunkW(outputBuf)).run.run

      outputBuf.toByteArray mustEqual "12345".getBytes
    }

    "read at most `length` bytes" in {
      val testData = new ByteArrayInputStream("123456789".getBytes)
      val fm = new FileModule{}
      val in = fm.chunkRRange(testData, 4096, 0, just(20))
      val outputBuf = new ByteArrayOutputStream()

      Process.constant(4096).toSource.through(in).to(io.chunkW(outputBuf)).run.run

      outputBuf.toByteArray mustEqual "123456789".getBytes
    }

    "read from `offset` bytes" in {
      val testData = new ByteArrayInputStream("123456789".getBytes)
      val fm = new FileModule{}
      val in = fm.chunkRRange(testData, 4096, 6, scalaz.Maybe.empty)
      val outputBuf = new ByteArrayOutputStream()

      Process.constant(4096).toSource.through(in).to(io.chunkW(outputBuf)).run.run

      outputBuf.toByteArray mustEqual "789".getBytes
    }

    "throw an `IndexOutOfBoundsException` if reading from `offset` after stream length" in {
      val testData = new ByteArrayInputStream("123456789".getBytes)
      val fm = new FileModule{}
      val in = fm.chunkRRange(testData, 4096, 20, scalaz.Maybe.empty)
      val outputBuf = new ByteArrayOutputStream()

      Process.constant(4096).toSource.through(in).to(io.chunkW(outputBuf)).run.run must throwAn[IndexOutOfBoundsException]
    }

    "read bytes between `offset` and `offset+length`" in {
      val testData = new ByteArrayInputStream("123456789".getBytes)
      val fm = new FileModule{}
      val in = fm.chunkRRange(testData, 4096, 2, just(2))
      val outputBuf = new ByteArrayOutputStream()

      Process.constant(4096).toSource.through(in).to(io.chunkW(outputBuf)).run.run

      outputBuf.toByteArray mustEqual "34".getBytes
    }
  }

}
