package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp, MonadCancel}
import com.singhraghav.catspractice.effects.utils._
import com.singhraghav.catspractice.effects.utils.generalized._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
object PolyMorphicMonadCancel extends IOApp.Simple {

  val inputPassword = IO("Input Password:").myDebug >> IO("typing password").myDebug >> IO.sleep(5.second) >> IO("bublaaa")

  val verifyPassword = (pw: String) => IO("verifying").myDebug >> IO.sleep(5.second) >> IO(pw == "bublaaa")

  val authFlow = IO.uncancelable { poll =>
    for {
      pw <- poll(inputPassword).onCancel(IO("authentication timed out. Try again later").myDebug.void)
      verified <- verifyPassword(pw)
      _ <- if (verified) IO("Authentication Successfull").myDebug else IO("Auth Failed").myDebug
    } yield ()
  }

  val authProgram = for {
    authFib <- authFlow.start
    _ <- IO.sleep(3.second) >> IO("Auth time out, cancelling ...").myDebug >> authFib.cancel
    _ <- authFib.join
  } yield ()

  import cats.syntax.flatMap._
  import cats.syntax.functor._
  def authFlowGeneralized[F[_], E](implicit mc: MonadCancel[F, E]): F[Unit] = {
    val inputPassword: F[String] = for {
      _ <- mc.pure("Input Password:").myDebug2
      _ <- mc.pure("typing password").myDebug2.unsafeSleep(5.seconds)
      _ <- mc.pure("password submitted").myDebug2
      password <- mc.pure("passi")
    } yield password

    def verifyPassword(pwd: String): F[Boolean] = for {
      _ <- mc.pure("Verifying Password:").myDebug2.unsafeSleep(5.seconds)
      result <- mc.pure(pwd == "passi")
    } yield result

    val authFlow: F[Unit] = mc.uncancelable { poll =>
      for {
        pw   <- mc.onCancel(poll(inputPassword), mc.pure("authentication timed out. Try again later").myDebug2.void)
        verified <- verifyPassword(pw)
        _ <- if (verified) mc.pure("Authentication Successfull").myDebug2 else mc.pure("Auth Failed").myDebug2
      } yield ()
    }

    authFlow
  }

  val authProgram2 = for {
    authFib <- authFlowGeneralized[IO, Throwable].start
    _ <- IO.sleep(6.second) >> IO("Auth time out, cancelling ...").myDebug >> authFib.cancel
    _ <- authFib.join
  } yield ()

  override def run: IO[Unit] = authProgram2
}
