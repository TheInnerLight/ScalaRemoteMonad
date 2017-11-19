package remotemonad

import remotemonad._
import cats._
import cats.data._
import cats.implicits._
import io.circe.Encoder
import io.circe.generic.semiauto._
import remote.RDevice
import shared._

object HelloMonad extends App {
  implicit val commandEncoder = deriveEncoder[Command]
  implicit val untypedProcedureEncoder = deriveEncoder[UntypedProcedure]
  implicit val packetEncoder = deriveEncoder[Packet]

  val test: Remote[Unit] = for {
    _ <- Remote.say("Howdy doodly do")
    _ <- Remote.say("How about a muffin?")
  } yield (())

  val test2: Remote[Unit] = for {
    t <- Remote.temperature
    _ <- Remote.say(s"$t")
    _ <- Remote.toast(4)
  } yield (())

  val test3: Remote[Unit] = for {
    _ <- Remote.say("Do you want some toast?")
    t <- Remote.temperature
    _ <- Remote.say(s"$t")
  } yield (())

  val program = for {
    _ <- Remote.send(RDevice.device)(test)
    _ <- Remote.send(RDevice.device)(test2)
    _ <- Remote.send(RDevice.device)(test3)
  } yield(())

  program.unsafeRunSync()
}
