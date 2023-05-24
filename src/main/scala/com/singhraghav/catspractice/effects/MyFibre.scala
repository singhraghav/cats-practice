package com.singhraghav.catspractice.effects

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.kernel.{Fiber, Outcome}
import cats.effect.{FiberIO, IO, IOApp}

import scala.concurrent.duration.FiniteDuration

object MyFibre extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._


  val meaningOfLife = IO.pure(42)
  val favLanguage = IO.pure("scala")

  def simpleThreadIO() = for {
    mol <- meaningOfLife.myDebug
    lang <- favLanguage.myDebug
  } yield ()

  def createFibre: Fiber[IO, Throwable, String] = ???

  val aFibre: IO[FiberIO[Int]] = meaningOfLife.myDebug.start

  val differentThreadIo = for {
    _ <- aFibre.myDebug
    lanf <- favLanguage.myDebug
  } yield lanf

  def  runOnSomeOtherTHread[A](io: IO[A]): IO[Outcome[IO, Throwable, A]] = for {
    fib <- io.start
    result <- fib.join
  } yield result


  val someIOOnOtherTHread = runOnSomeOtherTHread(meaningOfLife)
  val someResultFromOtherTHread = someIOOnOtherTHread.flatMap{
    case Succeeded(fa) => fa
    case Errored(e) => IO(0)
    case Canceled() => IO(0)
  }

  def throwANotherTHread = for {
    fib <- IO.raiseError(new RuntimeException("no number for you")).start
    result <- fib.join
  } yield result

  def runFibreIO[A](io: IO[A]): IO[A] = (for {
    fib <- io.start
    result <- fib.join
  } yield result) flatMap  {
    case Succeeded(fa) => fa
    case Errored(e) => IO.raiseError(e)
    case Canceled() => IO.raiseError(new RuntimeException("io execution was cancelled"))
  }

  def tupleIOs[A, B](ioa: IO[A], iob: IO[B]): IO[(A, B)] = {
    val result = for {
      fibA <- ioa.start
      fibB <- iob.start
      resultA <- fibA.join
      resultB <- fibB.join
    } yield (resultA, resultB)

    result.flatMap{
      case (Succeeded(ioa), Succeeded(iob)) => ioa.flatMap(a => iob.map(b => (a, b)))
      case (Errored(e1), _) => IO.raiseError(e1)
      case (_, Errored(e)) => IO.raiseError(e)
      case _ => IO.raiseError(new RuntimeException("one of the fibre was cancelled"))
    }
  }

  def timeout[A](io: IO[A], duration: FiniteDuration): IO[A] = {
    val computation = for {
      fib <- io.start
      _ <- IO.sleep(duration) >> fib.cancel
      result <- fib.join
    } yield result

    computation flatMap {
      case Succeeded(fa) => fa
      case Errored(e) => IO.raiseError(e)
      case Canceled() => IO.raiseError(new RuntimeException("io execution was cancelled"))
    }
  }
  override def run: IO[Unit] = throwANotherTHread.myDebug.void
}
