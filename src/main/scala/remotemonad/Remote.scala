package remotemonad

import cats.data._
import cats.effect._
import cats.implicits._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import shared._

case class Remote[T](remote : Kleisli[StateT[IO, List[Command], ?], Device, T])

object Remote {
  def runRemote[T](x : Remote[T]): ReaderT[({
    type Λ$[γ] = StateT[IO, List[Command], γ]
  })#Λ$, Device, T] = x match {
    case Remote(ka) => ka
  }

  def liftIO[T](iox : IO[T]) =
    Remote(Kleisli.lift[StateT[IO, List[Command], ?], Device, T](StateT.lift[IO, List[Command], T](iox)))

  def ask : Remote[Device] =
    Remote(Kleisli.ask[StateT[IO, List[Command], ?], Device])

  def get : Remote[List[Command]] =
    Remote(Kleisli {(device : Device) => StateT.get[IO, List[Command]]})

  def put(commands : List[Command]) : Remote[Unit] =
    Remote(Kleisli {(device : Device) => StateT.set(commands)})

  def say (message : String) : Remote[Unit] =
    sendCommand (Say(message))

  def send[T](device: Device)(remote : Remote[T])(implicit encoder: Encoder[Packet]): IO[Unit] = for {
    queueAndT <- Remote.runRemote(remote).run(device).run(List.empty)
    (commands, _) = queueAndT
    result <- commands match {
      case Nil => IO.pure(())
      case _   => {
        val asyncPacket = Packet.createAsync(commands)
        Device.async(device)(asyncPacket.asJson.noSpaces)
      }
    }
  } yield result

  def sendCommand(cmd : Command) : Remote[Unit] =
    Remote(Kleisli {(device : Device) => (StateT.modify ((lst : List[Command]) => cmd :: lst)) } )

  def sendProcedure[T](proc : Procedure[T])(implicit decoder:Decoder[T], encoder:Encoder[Packet]) : Remote[T] =
    for {
      device <- Remote.ask
      commands <- Remote.get
      resultStr <- Remote.liftIO(Device.sync(device)(Packet.createSync(commands, proc).asJson.noSpaces))
      _ <- Remote.put(List.empty)
    } yield (Procedure.readReply(proc)(resultStr))

  def temperature(implicit decoder:Decoder[Int], encoder:Encoder[Packet]): Remote[Int] = sendProcedure(Temperature)

  def toast (seconds : Int)(implicit decoder:Decoder[Unit], encoder:Encoder[Packet]): Remote[Unit] = sendProcedure[Unit](Toast(seconds))

}