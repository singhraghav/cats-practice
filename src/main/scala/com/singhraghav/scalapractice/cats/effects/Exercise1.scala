package com.singhraghav.scalapractice.cats.effects

import cats.effect.{IO, IOApp, Resource}
import cats.effect.std.Console

import scala.io.{BufferedSource, Source}

object Exercise1 extends IOApp.Simple {

  private val filePath = "src/main/resources/example.txt"
  def readFile(file: String): IO[String] =
    Resource.make[IO, BufferedSource](IO(Source.fromFile(file)))(source => IO(source.close()))
      .use(source => IO(source.getLines().mkString))

  private val fileContent = readFile(filePath).attempt.flatMap {
    case Right(value) => Console[IO].println(s"Read value from file: $value")
    case Left(error) => Console[IO].println(s"Error occurred while reading file ${error.getMessage}")
  }

  def getCacheValue(
                     key: String,
                     onSuccess: String => Unit,
                     onFailure: Throwable => Unit
                   ): Unit = ???

  def cacheValueIO(key: String): IO[String] =
    IO.async_[String] { cb =>
      getCacheValue(key, str => cb(Right(str)), error => cb(Left(error)))
    }
  override def run: IO[Unit] = fileContent

}
