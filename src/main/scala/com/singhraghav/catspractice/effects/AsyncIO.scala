package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.Try
object AsyncIO extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._

  val threadPool = Executors.newFixedThreadPool(8)
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(threadPool)

  def mol(): Int = {
    Thread.sleep(1000)
    println(s"[${Thread.currentThread().getName}] computing meaning of life on some other thread")
    42
  }
  def computeMeaningOfLife(): Either[Throwable, Int] = Try {
    Thread.sleep(1000)

    println(s"[${Thread.currentThread().getName}] computing meaning of life on some other thread")
    42
  }.toEither

  type Callback[A] = Either[Throwable, A] => Unit

  def computeMolOnThreadPool(computation: => Either[Throwable, Int]): Unit = {
    threadPool.execute(() => computation)
  }

  // lift async to IO
  val asyncMolIO: IO[Int] = IO.async_ { register =>
    threadPool.execute { () =>
      register(computeMeaningOfLife())
    }
  }

  def asyncToIO[A](computation: () => A)(ec: ExecutionContext): IO[A] = IO.async_ { cb =>
    ec.execute { () =>
      cb(Try(computation()).toEither)
    }
  }

  lazy val molFuture = Future(mol())(ec)

  val molFutureIO = IO.async_ { (cb: Callback[Int]) =>
    molFuture
      .onComplete((value: Try[Int]) => cb(value.toEither))(ec)
  }

  def demoAsyncCancellation() = {
    IO.async { (cb: Callback[Int]) =>
      // IO[Option[IO[Unit]]]

      IO(threadPool.execute { () =>
        cb(computeMeaningOfLife())
      }).as(Some(IO("cancelled").myDebug.void))
    }
  }

  val cancelled = for {
    fib <- demoAsyncCancellation().start
    _   <- IO.sleep(500.millis) >> IO("cancelling").myDebug >> fib.cancel
    _ <- fib.join
  } yield ()

  override def run: IO[Unit] = cancelled.myDebug >> IO.sleep(500.millis) >>IO(threadPool.shutdown())

}
