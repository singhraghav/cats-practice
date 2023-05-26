package com.singhraghav.catspractice.zioeffects

import zio.Clock.ClockLive
import zio._

import java.io.IOException
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
object InBuiltZIOEffects extends ZIOAppDefault {

  val meaningOfLife: ZIO[Any, Nothing, Int] = ZIO.succeed(20)

  val aFAilure: ZIO[Any, String, Nothing] = ZIO.fail("some failure")

  ZIO.fromOption(None)

  //page 48 converting async callbacks

  def getUserByIdAsync(id: Int)(cb: Option[String] => Unit): Unit = ???

  def getUserByIdAsyncZIO(id: Int): ZIO[Any, None.type , String] = ZIO.async { callback =>
    getUserByIdAsync(id) {
      case Some(name) => callback(ZIO.succeed(name))
      case None => callback(ZIO.fail(None))
    }
  }

  val currentTimeInMillis: ZIO[Any, IOException, Unit] = Clock.currentTime(TimeUnit.MILLISECONDS)
    .flatMap(time => Console.printLine(s"current tim ein millis is $time"))

  override def run = currentTimeInMillis


  // page 55
}
