package com.singhraghav.catspractice

object MyApplicatives extends App {

  // Applicatives is Functors  + pure method

  import cats.Applicative

  def productWithApplicative[F[_], A, B](fa: F[A], fb: F[B])(implicit applicative: Applicative[F]): F[(A, B)] = {
    val inter: F[B => (A, B)] = applicative.map(fa)(a => (b: B) => (a, b))
    applicative.ap(inter)(fb)
  }

}
