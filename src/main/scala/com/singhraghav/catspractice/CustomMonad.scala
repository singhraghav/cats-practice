package com.singhraghav.catspractice

import scala.annotation.tailrec

object CustomMonad extends App {

  import cats.Monad

  type Identity[T] = T

  implicit object IdentityMonad extends Monad[Identity] {
    override def pure[A](x: A): Identity[A] = x

    override def flatMap[A, B](fa: Identity[A])(f: A => Identity[B]): Identity[B] = f(fa)

    @tailrec
    override def tailRecM[A, B](a: A)(f: A => Identity[Either[A, B]]): Identity[B] = f(a) match {
      case Left(a) => tailRecM(a)(f)
      case Right(b) => b
    }
  }

  val anInt: Identity[Int] = 42

  import cats.syntax.flatMap._
  import cats.syntax.functor._
  import cats.syntax.applicative._

  val result: Identity[(Int, Int)] = {
  for {
    a <- anInt
    b <- 32.pure[Identity]
  } yield (a, b)
  }

  println(result)
}
