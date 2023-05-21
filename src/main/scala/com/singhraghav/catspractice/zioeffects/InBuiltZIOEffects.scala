package com.singhraghav.catspractice.zioeffects

import zio._
object InBuiltZIOEffects extends App {

  val meaningOfLife: ZIO[Any, Nothing, Int] = ZIO.succeed(20)

  val aFAilure: ZIO[Any, String, Nothing] = ZIO.fail("some failure")

  ZIO.fromOption(None)
}
