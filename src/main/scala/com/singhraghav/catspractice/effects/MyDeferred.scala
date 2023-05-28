package com.singhraghav.catspractice.effects

import cats.effect.{Deferred, IO, IOApp, Ref}
import com.singhraghav.catspractice.effects.utils._
import cats.syntax.parallel._
import cats.syntax.traverse._

import scala.concurrent.duration.DurationInt
object MyDeferred extends IOApp.Simple {

  val aDeferred: IO[Deferred[IO, Int]] =  Deferred[IO, Int]

  val reader: IO[Int] = aDeferred.flatMap(deferred => deferred.get)

  val writer = aDeferred.flatMap(deferred => deferred.complete(42))


  def deferredDemo(): IO[Unit] = {
    def consumer(signal: Deferred[IO, Int]): IO[Unit] =
      for {
        _ <- IO("consumer started").myDebug
        _ <- signal.get.myDebug
        _ <- IO("consumed message").myDebug
      } yield ()

    def producer(signal: Deferred[IO, Int]): IO[Unit] = for {
      _ <- IO("producer started").myDebug
      _ <- IO.sleep(1.second)
      generated <- IO(scala.util.Random.nextInt())
      _ <- signal.complete(generated)
      _ <- IO(s"produced $generated").myDebug
    } yield ()

    for {
      deferred <- IO.deferred[Int]
      _        <- (consumer(deferred), producer(deferred)).parTupled
    } yield ()
  }

  val fileParts = List("I ", "love S", "cala ", "with Cat", "s Effect! <EOF>")

  def fileNotifierWithRef(): IO[Unit] = {
    def downloadFile(contentRef: Ref[IO, String]): IO[Unit] = fileParts.map { part=>
      IO(s"[downloader] got '$part'" ).myDebug >> IO.sleep(1.second) >> contentRef.update(_ + part)
    }.sequence.void

    def notifyFileComplete(contentRef: Ref[IO, String]): IO[Unit] = for {
      file <- contentRef.get
      _ <- if(file.endsWith("<EOF>")) IO("[notifier] File download  complete").myDebug else IO("[notifier] downloading").myDebug >> IO.sleep(500.millis) >> notifyFileComplete(contentRef)
    } yield ()

    for {
      ref <- IO.ref("")
      fileDownloader <- downloadFile(ref).start
      notifier <- notifyFileComplete(ref).start
      _ <- notifier.join
      _ <- fileDownloader.join
    } yield ()
  }

  def fileNotifierWithDeferred(): IO[Unit] = {
    def downloadFile(contentRef: Ref[IO, String], signal: Deferred[IO, Unit]): IO[Unit] = fileParts.map { part =>
      IO(s"[downloader] got '$part'").myDebug >> IO.sleep(1.second) >> contentRef.update(_ + part)
    }.sequence.void.flatMap(_ => signal.complete(()).void)

    def notifyFileComplete(contentRef: Ref[IO, String], signal: Deferred[IO, Unit]): IO[Unit] = for {
      _ <- IO("[notifier] downloading file").myDebug
      _ <- signal.get
      file <- contentRef.get
      _ <-  IO(s"[notifier] File download complete - $file ").myDebug
    } yield ()

    for {
      ref <- IO.ref("")
      signal <- Deferred[IO, Unit]
      fileDownloader <- downloadFile(ref, signal).start
      notifier <- notifyFileComplete(ref, signal).start
      _ <- notifier.join
      _ <- fileDownloader.join
    } yield ()
  }


  def pureAlarm() = {
    def alarmCounter(signal: Deferred[IO, Unit], counter: Ref[IO, Int]): IO[Unit] = {
      for {
        _ <- IO.sleep(1.second)
        counterValue <- counter.updateAndGet(_ + 1)
        _ <- IO(s"[counter] incremented counter to $counterValue").myDebug
        _ <- if(counterValue == 10) IO("[counter] completed count to 10").myDebug >> signal.complete(()) else IO.defer(alarmCounter(signal, counter))
      } yield ()
    }

    def alarmSinger(signal: Deferred[IO, Unit]): IO[Unit] = for {
      _ <- IO("[alarm] waiting for counter to complete").myDebug
      _ <- signal.get
      _ <- IO("time's up").myDebug
    } yield ()

    for {
      counter <- Ref.of[IO, Int](0)
      signal  <- Deferred[IO, Unit]
      singerFib <- alarmSinger(signal).start
      counterFib <- alarmCounter(signal, counter).start
      _          <- singerFib.join
      _          <- counterFib.join
    } yield ()
  }
  override def run: IO[Unit] = pureAlarm()
}
