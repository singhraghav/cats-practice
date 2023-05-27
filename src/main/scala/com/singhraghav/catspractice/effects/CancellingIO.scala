package com.singhraghav.catspractice.effects

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object CancellingIO extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._

  val chainOfIos: IO[Int] = IO("waiting").myDebug >> IO.canceled >> IO(42).myDebug

  val paymentSystem: IO[String] =
    (IO("payment running, don;t cancel").myDebug >> IO.sleep(1.second) >> IO("Payment Completed").myDebug)
      .onCancel(IO("Cancel OF Doom").myDebug.void)

  val cancellationOfPayment = for {
    fib <- paymentSystem.start
    _ <- IO.sleep(500.millis) >> fib.cancel
    _ <- fib.join
  } yield ()

  val atomicPayment: IO[String] = IO.uncancelable(_ => paymentSystem)
  val atomicPayment2: IO[String] = paymentSystem.uncancelable

  val cancellationOfPayment2 = for {
    fib <- atomicPayment2.start
    _ <- IO.sleep(500.millis) >> IO("attempting cancellation").myDebug >> fib.cancel
    _ <- fib.join
  } yield ()

  val inputPassword = IO("Input Password:").myDebug >> IO("typing password").myDebug >> IO.sleep(5.second) >> IO("bublaaa")

  val verifyPassword = (pw: String) => IO("verifying").myDebug >> IO.sleep(5.second) >> IO(pw == "bublaaa")

  val authFlow = IO.uncancelable{poll =>
    for {
      pw <- poll(inputPassword).onCancel(IO("authentication timed out. Try again later").myDebug.void)
      verified <- verifyPassword(pw)
      _ <- if(verified) IO("Authentication Successfull").myDebug else IO("Auth Failed").myDebug
    } yield ()
  }

  val authProgram = for {
    authFib <- authFlow.start
    _ <- IO.sleep(6.second) >> IO("Auth time out, cancelling ...").myDebug >> authFib.cancel
    _ <- authFib.join
  } yield ()
  override def run: IO[Unit] = authProgram
}
