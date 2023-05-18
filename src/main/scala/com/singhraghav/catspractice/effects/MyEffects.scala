package com.singhraghav.catspractice.effects

import scala.io.StdIn.readLine

object MyEffects extends App {

  /*
  * The effect should describe what action will be performed
  * Effect should express what value it will generate
  * If there is a side effect the create of Effect should be separated from execution of side effect
  * */

  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))

    def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
  }

  def currentTimeMillis: MyIO[Long] = MyIO(() => System.currentTimeMillis())

  def measure[A](computation: MyIO[A]): MyIO[Long] =
    for {
      startTime <- currentTimeMillis
      value     <- computation
      endTime   <- currentTimeMillis
    } yield (endTime - startTime)

  def print(value: String): MyIO[Unit] = MyIO(() => println(value))

  def readInput(): MyIO[String] = MyIO(() => readLine())
  println(measure(currentTimeMillis).unsafeRun())

  readInput()
  println("Next to read input")
  println(measure(readInput().flatMap(print)).unsafeRun())
}
