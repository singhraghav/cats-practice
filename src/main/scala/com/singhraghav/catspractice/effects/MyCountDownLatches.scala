package com.singhraghav.catspractice.effects

import cats.effect.std.CountDownLatch
import cats.effect.{IO, IOApp}

import scala.concurrent.duration._
import com.singhraghav.catspractice.effects.utils._
import cats.syntax.parallel._
object MyCountDownLatches extends IOApp.Simple {

  def trigger(latch: CountDownLatch[IO]): IO[Unit] = for {
    _ <- IO("starting race shortly").myDebug >> IO.sleep(2.seconds)
    _ <- IO("5....").myDebug >> IO.sleep(1.seconds)
    _ <- latch.release
    _ <- IO("4....").myDebug >> IO.sleep(1.seconds)
    _ <- latch.release
    _ <- IO("3....").myDebug >> IO.sleep(1.seconds)
    _ <- latch.release
    _ <- IO("2....").myDebug >> IO.sleep(1.seconds)
    _ <- latch.release
    _ <- IO("1....").myDebug >> IO.sleep(1.seconds)
    _ <- latch.release
    _ <- IO("go go go ....").myDebug
  } yield ()

  def createRunner(id: Int, latch: CountDownLatch[IO]): IO[Unit] = for {
    _ <- IO(s"[runner $id] waiting for signal ... ").myDebug
    _ <- latch.await
    _ <- IO(s"[runner $id] running now  ... ").myDebug
  } yield ()


  def race(): IO[Unit] = for {
    latch <- CountDownLatch[IO](5)
    announcerFib <- trigger(latch).start
    _ <- (1 to 10).toList.parTraverse(createRunner(_, latch))
    _ <- announcerFib.join
  } yield ()
  override def run: IO[Unit] = race()
}
