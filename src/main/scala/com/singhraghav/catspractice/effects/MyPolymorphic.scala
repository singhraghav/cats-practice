//package com.singhraghav.catspractice.effects
//
//import cats.data.OptionT
//import cats.effect.kernel.Poll
//import cats.{Applicative, Monad}
//import cats.effect.{IO, IOApp}
//
//import scala.concurrent.duration._
//import com.singhraghav.catspractice.effects.utils._
//import cats.syntax.parallel._
//
//object MyPolymorphic extends IOApp.Simple {
//
//  trait MyApplicativeError[F[_], E] extends Applicative[F] {
//    def raiseError[A](error: E): F[A]
//
//    def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
//  }
//
//  trait MyMonadError2[F[_], E] extends MyApplicativeError[F, E] with Monad[F]
//
//  trait MyPoll[F[_]] {
//    def apply[A](fa: F[A]): F[A]
//  }
//  trait MyMonadCancel[F[_], E] extends MyMonadError2[F, E] {
//    def cancelled: F[Unit]
//
//    def uncancellable[A](poll: Poll[F]): F[A]
//  }
//
//  val optionT: OptionT[IO, String] = OptionT(IO(Some("hello")))
//
//  override def run: IO[Unit] = ???
//}
