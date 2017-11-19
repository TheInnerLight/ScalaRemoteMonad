package remotemonad

import cats.Monad
import cats.data.{Kleisli, StateT}
import cats.effect.IO
import shared.Command

package object `package` {

  implicit def remoteMonad[F[_]] = new Monad[Remote] {
    override def pure[A](x: A): Remote[A] = Remote(Kleisli.pure(x))

    override def flatMap[A, B](fa: Remote[A])(f: (A) => Remote[B]): Remote[B] =
      Remote(Remote.runRemote(fa).flatMap(f.andThen(Remote.runRemote(_))))

    override def tailRecM[A, B](a: A)(f: (A) => Remote[Either[A, B]]): Remote[B] =
      Remote(Monad[Kleisli[StateT[IO, List[Command], ?], Device, ?]].tailRecM(a)(f.andThen(Remote.runRemote(_))))
  }
}