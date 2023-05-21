package com.singhraghav.catspractice.effects

import cats.effect.IO

object ErrorHandling extends App {

  val aFailedCompute: IO[Int] = IO.delay(throw new RuntimeException("Some Error"))

  def option2IO[A](option: Option[A])(ifEmpty: Throwable): IO[A] = IO {
    option match {
      case None => IO.raiseError[A](ifEmpty)
      case Some(v) => IO.delay(v)
    }
  }.flatten

  def option2IO_v2[A](option: Option[A])(ifEmpty: Throwable): IO[A] = IO.fromOption(option)(ifEmpty)

  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] = io.handleError(handler)
}
