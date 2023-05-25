package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp}
import com.singhraghav.catspractice.effects.utils.IODebug

import java.io.{File, FileReader}
import java.util.Scanner
import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt

object MyResources extends IOApp.Simple {

  class Connection(url: String) {
    def open: IO[String] =  IO(s"opening connection on $url").myDebug

    def close: IO[String] = IO(s"closing connection on $url").myDebug
  }

  val asyncFetchUrl = for {
    fib <- (new Connection("google.com").open *> IO.sleep(Int.MaxValue.seconds)).start
    _ <- IO.sleep(1.second) *> fib.cancel
  } yield ()

  val correctAsyncFetch = for {
    conn <- IO(new Connection("yahoo.com"))
    fib <- (conn.open *> IO.sleep(Int.MaxValue.seconds)).onCancel(conn.close.void).start
    _ <- IO.sleep(1.second) *> fib.cancel
  } yield ()

  //bracket pattern

  val bracketFetchUrl = IO(new Connection("damru.com"))
    .bracket(conn => conn.open *> IO.sleep(Int.MaxValue.seconds))(conn => conn.close.void)

  val bracketProgram = for {
    fib <- bracketFetchUrl.start
    _ <- IO.sleep(1.second) *> fib.cancel
  } yield ()

  def openFile(path: String): IO[Scanner] = IO(new Scanner(new FileReader(new File(path))))

  def bracketReadFile(path: String): IO[Unit] = openFile(path).bracket { scanner =>
    def readingPrintingAndSleeping(): IO[Unit] =
      if (scanner.hasNextLine) IO(scanner.nextLine()).myDebug >> IO.sleep(100.millis) >> readingPrintingAndSleeping()
      else IO.unit
    readingPrintingAndSleeping()
  }( scanner => IO(println(s"closing file at path $path")).myDebug *> IO(scanner.close()))
  override def run: IO[Unit] = bracketReadFile("src/main/resources/example.txt").void
}
