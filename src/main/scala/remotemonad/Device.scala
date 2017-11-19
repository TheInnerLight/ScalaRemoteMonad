package remotemonad

import cats.effect.IO

case class Device(sync : String => IO[String], async : String => IO[Unit])

object Device {
  def sync(device: Device): (String) => IO[String] = device match {
    case Device(sync, _) => sync
  }

  def async(device: Device): (String) => IO[Unit] = device match {
    case Device(_, async) => async
  }
}