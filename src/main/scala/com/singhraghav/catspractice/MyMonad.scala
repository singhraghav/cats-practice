package com.singhraghav.catspractice

import cats.implicits.catsStdInstancesForFuture

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object MyMonad extends App {

  val numberList = List(1, 2, 3)
  val charList = List('a', 'b', 'c')

  // All combination
  val allCombination: List[(Int, Char)] = {
  numberList.flatMap(num => charList.map(c => (num, c)))
  }

  println(allCombination)

  val numberOption = Option(2)
  val charOption = Option('d')

  val comb = numberOption.flatMap(n => charOption.map(c => (n, c)))

  println(comb)

  trait MyMonad[M[_]] {
    def pure[A](value: A): M[A]

    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

    def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(a => pure(f(a)))
  }

  import cats.Monad
  import cats.instances.option._
  def optionMonad = Monad[Option]

  val anOption = optionMonad.pure(4)

  import cats.instances.future._

  val futureMonad = Monad[Future]
  val aFuture = futureMonad.pure(2)
  val futureTransformation = futureMonad.flatMap(aFuture)(xa => Future(xa + 1))

  println(Await.result(futureTransformation, Duration.Inf))

  //specialized API
  def getPairs[M[_], A, B](group1: M[A], group2: M[B])(implicit monad: Monad[M]): M[(A, B)] = monad.flatMap(group1)(a => monad.map(group2)(b => (a, b)))

  println(getPairs(numberList, charList))
  println(getPairs(numberOption, charOption))
}
