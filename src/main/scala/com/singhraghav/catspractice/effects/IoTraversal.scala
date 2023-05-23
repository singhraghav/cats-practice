package com.singhraghav.catspractice.effects

import cats.Traverse
import cats.effect.{IO, IOApp}

object IoTraversal extends IOApp.Simple {

  def sequence[A](listOfIos: List[IO[A]]): IO[List[A]] = {
    import cats.syntax.parallel._
    listOfIos.parTraverse(identity)
  }

  def sequence_v2[F[_]: Traverse, A](ios: F[IO[A]]): IO[F[A]] = {
    Traverse[F].traverse(ios)(identity)
  }

  override def run: IO[Unit] = ???
}
