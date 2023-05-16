package com.singhraghav.catspractice.typeclasses

object MyKleisli extends App {

  val func1: Int => Option[String] = x => if (x % 2 == 0) Some(s"$x is even") else None

  val func2: Int => Option[Int] = x => Some(x * 3)

  // in order to chain function which returns wrapper you need kliesli
  // do something like func2 andThen func1


  import cats.data.Kleisli

  val func1K: Kleisli[Option, Int, String] = Kleisli(func1)
  val func2K: Kleisli[Option, Int, Int] = Kleisli(func2)

  val func3k: Kleisli[Option, Int, String] = func2K andThen func1K
}
