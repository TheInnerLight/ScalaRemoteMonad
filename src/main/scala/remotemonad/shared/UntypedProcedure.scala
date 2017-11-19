package remotemonad.shared

sealed trait UntypedProcedure

case object Temperature extends UntypedProcedure
case class Toast(time : Int) extends UntypedProcedure