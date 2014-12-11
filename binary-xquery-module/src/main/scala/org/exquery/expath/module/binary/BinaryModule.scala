
case class XPathErrorCode(code: String) //TODO use QName for the code


class Hex {

  private val validator = "([0-9A-Fa-f]+)".r

  def hex(in: Option[String]) : Either[XPathErrorCode, Option[Iterator[Byte]]] = {
    in.flatMap(validHex(_).right.map {
      hex =>
        new Iterator[Byte] {
          val it = leftPad(hex).sliding(2) //2 hex digits to a byte ;-)

          override def next(): Byte = Integer.parseInt(it.next(), 16).toByte

          override def hasNext: Boolean = it.hasNext
        }
    })
  }

  /**
   * Validate the input is valid s
   *
   * @param in A string to validate
   *
   * @return Some hexidecimal string, or None
   */
  private def validHex(in: String) : Either[XPathErrorCode, String] = validator findFirstIn in match {
    case Some(validator(hex)) =>
      Some(hex)
    case None =>
      None
  }

  /**
   * Left pads the String to an even number of characters
   * by inserting a 0 at the front of a string
   * with an un-even number of characters
   * 
   * @param s The string to potentially pad
   *
   * @return A String with an even number of chars
   */
  private def leftPad(s: String) : String = {
    if(s.length % 2 == 0) {
      s
    } else {
      "0" + s
    }
  }
}