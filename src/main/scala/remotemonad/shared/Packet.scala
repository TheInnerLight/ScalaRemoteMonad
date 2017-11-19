package remotemonad.shared

import remotemonad.Procedure

case class Packet(commands : List[Command], procedure: Option[UntypedProcedure])

object Packet{
  def createAsync(commands : List[Command]) =
    Packet(commands, None)

  def createSync[T](commands : List[Command], proc : Procedure[T]) = proc match {
    case remotemonad.Temperature => Packet(commands, Some(Temperature))
    case remotemonad.Toast(time) => Packet(commands, Some(Toast(time)))
  }
}