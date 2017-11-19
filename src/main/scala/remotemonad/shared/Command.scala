package remotemonad.shared

sealed trait Command

case class Say(message : String) extends Command
