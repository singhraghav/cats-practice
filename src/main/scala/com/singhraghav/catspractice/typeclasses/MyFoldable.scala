package com.singhraghav.catspractice.typeclasses

import cats.Monoid
import cats.implicits.catsSyntaxSemigroup

object MyFoldable extends App {

  object ListExercises {
    def map[A, B](list: List[A])(f: A => B): List[B] =
      list.foldLeft(List.empty[B])((acc, curr) => f(curr) :: acc).reverse

    def combineAll[A](list: List[A])(implicit monoid: Monoid[A]): A = list.foldLeft(monoid.empty)((acc, curr) => acc |+| curr)
  }

  import cats.Foldable
  import cats.instances.list._

  Foldable[List]

}
