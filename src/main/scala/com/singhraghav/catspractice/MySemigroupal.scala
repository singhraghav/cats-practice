package com.singhraghav.catspractice

import cats.Monad

object MySemigroupal extends App {

  def productWithMonads[F[_], A, B](fa: F[A], fb: F[B])(implicit monad: Monad[F]): F[(A, B)] = {
    import cats.syntax.flatMap._
    import cats.syntax.functor._
    fa.flatMap(a => fb.map(b => (a, b)))
    for {
      a <- fa
      b <- fb
    } yield (a, b)
  }

  trait CustomSemiGroupal[F[_]] {
    def product[A, B](a: F[A], b: F[B]): F[(A, B)]
  }

  // Monads extends SemiGroupal

  import cats.Semigroupal

  implicit val zipListSemiGroupal = new Semigroupal[List] {
    override def product[A, B](fa: List[A], fb: List[B]): List[(A, B)] = fa.zip(fb)
  }


  println(Semigroupal[List].product(List(1, 2), List("a", "b")))

}
