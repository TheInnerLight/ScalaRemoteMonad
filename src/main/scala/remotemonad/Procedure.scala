package remotemonad

import io.circe.parser.decode
import io.circe.Decoder

sealed trait Procedure[T]

case object Temperature extends Procedure[Int]
case class Toast(time : Int) extends Procedure[Unit]

object Procedure {
  def readReply[T] (procedure: Procedure[T])(str : String)(implicit decoder: Decoder[T]) : T = decode[T](str).getOrElse(throw new Exception("Deserialistion failed"))
}
