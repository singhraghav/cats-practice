package com.singhraghav.catspractice.effects
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
object BlockingIO extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._

  val someSleeps = for {
    _ <- IO.sleep(5.second) >> IO(println("sleep completed"))
    _ <- IO(println(s"Computation in middle"))
    _ <- IO.sleep(1.second)
  } yield ()

  val aBlockingIo = IO.blocking{
    Thread.sleep(1000)
    println(s"[${Thread.currentThread().getName}] computed a blocking code")
    42
  }

  override def run: IO[Unit] = someSleeps.void
}
