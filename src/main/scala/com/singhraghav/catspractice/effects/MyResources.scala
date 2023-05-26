package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp, Resource}
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

  def readingPrintingAndSleeping(scanner: Scanner): IO[Unit] =
    if (scanner.hasNextLine) IO(scanner.nextLine()).myDebug >> IO.sleep(100.millis) >> readingPrintingAndSleeping(scanner)
    else IO.unit
  def bracketReadFile(path: String): IO[Unit] = openFile(path).bracket { scanner =>
    readingPrintingAndSleeping(scanner)
  }( scanner => IO(println(s"closing file at path $path")).myDebug *> IO(scanner.close()))

  val connectionResource = Resource.make(IO(new Connection("google.com")))(conn => conn.close.void)

  val resourceFetchUrl = for {
    fib <- connectionResource.use(con => con.open >> IO.never).start
    _ <- IO.sleep(1.second) >> fib.cancel
  } yield ()

  def openFileResource(path: String): Resource[IO, Scanner] = Resource.make(openFile(path))(scanner => IO(scanner.close()))

  val readFileWIthResource: String => IO[Unit] = string =>
    openFileResource(string).use(scanner => readingPrintingAndSleeping(scanner))
  override def run: IO[Unit] = readFileWIthResource("src/main/resources/example.txt")
}
