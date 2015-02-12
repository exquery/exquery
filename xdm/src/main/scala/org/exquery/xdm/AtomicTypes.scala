/**
 * Copyright © 2014, Adam Retter
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
package org.exquery.xdm

trait AnyAtomicType extends Item with AnySimpleType

trait UntypedAtomic extends AnyAtomicType

trait DateTime extends AnyAtomicType

trait Date extends AnyAtomicType

trait Time extends AnyAtomicType

trait Duration extends AnyAtomicType

trait YearMonthDuration extends Duration

trait DayTimeDuration extends Duration

trait Float extends AnyAtomicType

trait Double extends AnyAtomicType

//<editor-fold desc="Decimal Atomic Types">
trait Decimal extends AnyAtomicType

trait Integer extends Decimal

trait NonPositiveInteger extends Integer

trait NegativeInteger extends NonPositiveInteger

trait Long extends Integer

trait Int extends Long

trait Short extends Int

trait Byte extends Short

trait NonNegativeInteger extends Integer

trait UnsignedLong extends NonNegativeInteger

trait UnsignedInt extends UnsignedLong

trait UnsignedShort extends UnsignedInt

trait UnsignedByte extends UnsignedShort

trait PositiveInteger extends NonNegativeInteger
//</editor-fold>

//<editor-fold desc="Gregorian Atomic Types">
trait GYearMonth extends AnyAtomicType

trait GYear extends AnyAtomicType

trait GMonthDay extends AnyAtomicType

trait GDay extends AnyAtomicType

trait GMonth extends AnyAtomicType
//</editor-fold>

trait Boolean extends AnyAtomicType

trait Base64Binary extends AnyAtomicType

trait HexBinary extends AnyAtomicType

trait AnyURI extends AnyAtomicType

trait QName extends AnyAtomicType

//useful for pattern matching
object QName {
  import java.lang.{String => JString}
  import javax.xml.namespace.{QName => JQName}

  def apply(namespaceURI: JString, localPart: JString) = new JQName(namespaceURI, localPart)
  def apply(namespaceURI: JString, localPart: JString, prefix: JString) = new JQName(namespaceURI, localPart, prefix)
  def apply(localPart: JString) = new JQName(localPart)

  def unapply(qn: JQName) : Option[(JString, JString, JString)] = Some(qn.getNamespaceURI, qn.getLocalPart, qn.getPrefix)
}

trait Notation extends AnyAtomicType

trait String extends AnyAtomicType

trait NormalizedSpace extends String

trait Token extends NormalizedSpace

trait Language extends Token

trait NMToken extends Token

trait Name extends Token

trait NCName extends Name

trait ID extends NCName

trait IDRef extends NCName

trait Entity extends NCName



