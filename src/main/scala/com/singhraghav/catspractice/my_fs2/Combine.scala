package com.singhraghav.catspractice.my_fs2

import cats.effect.{IO, IOApp}
import fs2._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object Combine extends IOApp.Simple {
  override def run: IO[Unit] = {
    val s = Stream.repeatEval(IO.println("emitting...") *> IO(42))

    s.take(10).compile.toList.flatMap(IO.println)

    val s2: Stream[IO, Int] =
      for {
        x <- Stream.eval(IO(42))
        y <- Stream.eval(IO(x + 1))
      } yield x + y

    s2.compile.toList.flatMap(IO.println)


    def evalEvery[A](d: FiniteDuration)(fa: IO[A]): Stream[IO, A] = Stream.repeatEval(IO.sleep(d) *> fa)

    def evalEvery_v2[A](d: FiniteDuration)(fa: IO[A]): Stream[IO, A] = (Stream.sleep_[IO](d) ++ Stream.eval(fa)).repeat

    evalEvery_v2(1.second)(IO.println("running stream.."))
      .compile
      .drain


  }
}
