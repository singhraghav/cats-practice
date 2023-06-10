package com.singhraghav.scalapractice.zio

import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, Console}

import scala.io.Source

object Exercise1 extends ZIOAppDefault {

  private val filePath = "src/main/resources/example.txt"

  def readFileZIO(fileName: String): ZIO[Any, Throwable, Unit] = {
    ZIO.acquireReleaseWith(ZIO.attempt(Source.fromFile(fileName)))(reader => ZIO.attempt(reader.close()).ignore) (
      source => ZIO.attempt(source.getLines().mkString).flatMap(content => Console.printLine(content))
    )
  }

  def getCacheValue(
                     key: String,
                     onSuccess: String => Unit,
                     onFailure: Throwable => Unit
                   ): Unit = ???

  def getCacheZIO(key: String): ZIO[Any, Throwable, String] =
    ZIO.async[Any, Throwable, String] { register =>
      getCacheValue(key, str => register(ZIO.succeed(str)), failure => register(ZIO.fail(failure)))
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = readFileZIO(filePath)
}
