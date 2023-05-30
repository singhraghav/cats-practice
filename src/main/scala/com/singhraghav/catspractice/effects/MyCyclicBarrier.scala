package com.singhraghav.catspractice.effects

import cats.effect.std.CyclicBarrier
import cats.effect.{IO, IOApp}

import scala.concurrent.duration._
import com.singhraghav.catspractice.effects.utils._
import cats.syntax.parallel._

object MyCyclicBarrier extends IOApp.Simple {

  def createUser(id: Int, barrier: CyclicBarrier[IO]): IO[Unit] = for {
    _ <- IO.sleep(1.second)
    _ <- IO(s"[user $id] - signing up for wait list ...").myDebug
    _ <- IO.sleep(2.second)
    _ <- IO(s"[user $id] - on the wait list now ...").myDebug
    _ <- barrier.await
    _ <- IO(s"[user $id] - waoooo I am in  ...").myDebug
  } yield ()


  def openNetwork(): IO[Unit] = for {
    _ <- IO(s"social network will start after 10 users ...").myDebug
    barrier <- CyclicBarrier[IO](10)
    _ <- (1 to 20).toList.parTraverse(id => createUser(id, barrier))
  } yield ()
  override def run: IO[Unit] = openNetwork()
}
