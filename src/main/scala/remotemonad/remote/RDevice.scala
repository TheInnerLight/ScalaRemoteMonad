package remotemonad.remote

import cats._
import cats.data._
import cats.implicits._
import cats.syntax.traverse
import cats.effect._
import remotemonad.Device
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.Decoder
import io.circe.generic.semiauto._
import remotemonad.shared._

import scala.util.Random

/**
  * Created by Phil on 19-Nov-17.
  */
object RDevice {
  implicit val rCommandDecoder = deriveDecoder[Command]
  implicit val rProcedureDecoder = deriveDecoder[UntypedProcedure]
  implicit val rpacketDecoder = deriveDecoder[Packet]

  val parsePacket = (json: String) => decode[Packet](json).getOrElse(throw new Exception("Remote: deserialisation failed"))

  val device: Device = Device(parsePacket.andThen(execPacket), parsePacket.andThen(execPacket).andThen(_.map(_ => ())))

  def execPacket(packet: Packet) : IO[String] = packet match {
    case Packet(commands, proc) => {
      for {
        _ <- commands.traverse_(execCommand)
        str <- proc match {case Some(proc) => execProcedure(proc); case None => IO.pure(().asJson.noSpaces)}
      } yield (str)
    }
  }

  def execCommand (command : Command): IO[Unit] = command match {
    case Say(str) => IO[Unit]{println(str)}
  }

  def execProcedure(procedure: UntypedProcedure) : IO[String] = procedure match {
    case Temperature => IO{
      val random = new Random()
      (random.nextInt(50) + 50).asJson.noSpaces
    }
    case Toast(time) => IO {
      println("Remote: Toasting...")
      Thread.sleep(1000 * time)
      println("Remote : Done!")
      ().asJson.noSpaces
    }
  }

}
