package com.singhraghav.catspractice.effects

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object RacingIO extends IOApp.Simple {
  import com.singhraghav.catspractice.effects.utils._

  def runWithSleep[A](value: A, duration: FiniteDuration): IO[A] = {
    (
    IO(s"starting computation $value ").myDebug >>
      IO.sleep(duration) >>
      IO(s"Computation $value done") >>
      IO(value)
    ).onCancel(IO(s"Computation $value cancelled").myDebug.void)
  }

  def testRace() = {
    val meaningOfLife = runWithSleep(42, 1.second)
    val favLang = runWithSleep("Scala", 3.second)

    val first: IO[Either[Int, String]] = IO.race(meaningOfLife, favLang)

    first.flatMap {
      case Left(mol) => IO(s"meaning of Life won $mol").myDebug
      case Right(lang) => IO(s"fav lang won $lang").myDebug
    }
  }

  def timeout[A](io: IO[A], duration: FiniteDuration): IO[A] = {
    IO.race(io, IO.sleep(duration))
      .flatMap {
        case Left(value) => IO(s"io won returning $value").myDebug >> IO(value)
        case Right(_)    => IO(s"io timed out raising error").myDebug >> IO.raiseError(new Exception("IO timed out"))
      }
  }

  def unrace[A, B](ioa: IO[A], iob: IO[B]): IO[Either[A, B]] = {
    IO.racePair(ioa, iob)
      .flatMap {
        case Left((outcomeA, fibB)) => fibB.join >> iob.map(b => Right(b))
        case Right((fibA, outcomeB)) => fibA.join.flatMap {
          case Succeeded(fa) => fa.map(a => Left(a))
          case _ => IO.raiseError(new Exception("computation for a lost but failed"))
        }
      }
  }
  override def run: IO[Unit] =  testRace.void
}
