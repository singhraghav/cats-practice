package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp}

object Parralelism extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._

  val sequentialEffect1 = IO("Hello there")


  val sequentialEffect2 = IO("Hello there 2")

  val combineEffect1 = for {
    a <- sequentialEffect1.myDebug
    b <- sequentialEffect2.myDebug
  } yield a + b

  import cats.syntax.apply._
  val combineEffect2 = (sequentialEffect2.myDebug, sequentialEffect1.myDebug).mapN((eff2, eff1) => eff1 + eff2)

  import cats.syntax.parallel._
  val combineEffect3 = (sequentialEffect1.myDebug, sequentialEffect2.myDebug).parMapN((e1, e2) => e1 + " " + e2)

  override def run: IO[Unit] = combineEffect3.myDebug.map(println)

  val anisIO = IO(println)
}
