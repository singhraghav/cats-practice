package com.singhraghav.catspractice.effects

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.unsafe.implicits.global

object InbuiltIO extends App {

  val ourFirstIO: IO[Int] = IO.pure(42) // arg to pure should not have side effects

  val delayedComputation: IO[Int] = IO.delay{println("unsafe computation"); 54}

  val shouldNotDoIt = IO.pure({println("wrapped side effect in pure effect"); 56})

  val delayedComputation2: IO[Int] = IO{println("unsafe computation 2"); 54}

  def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ioa.flatMap(_ => iob)

  def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    for {
      a <- ioa
      _ <- iob
    } yield a

  def forever[A](io: IO[A]): IO[A] = io.flatMap(_ => forever(io))

  def convert[A, B](ioa: IO[A], value: B): IO[B] = ioa.map(_ => value)

  def asUnit[A](io: IO[A]): IO[Unit] = convert(io, ())

  def sum(n: Int): Int = if(n <= 0) 0 else sum(n-1) + n

  def sumIo(n: Int): IO[Int] = if (n <= 0) IO(0)
  else for {
    last <- IO(n)
    sum <- sumIo(n - 1)
  } yield last + sum


  println(delayedComputation2.unsafeRunSync())
  println(sumIo(20000).unsafeRunSync())
}

object FirstCatsEffectApp extends IOApp {
  val app = IO(println("app"))
  override def run(args: List[String]): IO[ExitCode] = app.as(ExitCode.Success)
}
