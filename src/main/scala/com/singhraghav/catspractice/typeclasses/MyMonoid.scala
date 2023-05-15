package com.singhraghav.catspractice.typeclasses

object MyMonoid extends App {

  // implement reduce by fold
  import cats.Monoid
  import cats.syntax.monoid._
  def combineFold[T: Monoid](list: List[T]): T = list.foldRight(Monoid[T].empty)(_ |+| _)

  val myList= List(1,2,3,4)

  import cats.instances.int._

  println(combineFold(myList))

  val phoneBooks = List(
    Map("Alice" -> 235, "Bob" -> 647),
    Map("Charlie" -> 372, "Daniel" -> 889),
    Map("Tina" -> 123, "Charlie" -> 777)
  )
//  import cats.instances.map._

  implicit val monoidMyMap = Monoid.instance[Map[String, Int]](Map.empty[String, Int], (m1, m2) => m1 ++ m2)
  println(combineFold(phoneBooks))

  //Exercise 3

  case class ShoppingCart(cart: List[String], amount: Double)

  object ShoppingCart {
    def empty: ShoppingCart = ShoppingCart(Nil, 0)
    implicit val shoppingCartMonoid: Monoid[ShoppingCart] = Monoid.instance(ShoppingCart.empty, (c1, c2) => ShoppingCart(c1.cart ++ c2.cart, c1.amount + c2.amount))
  }

  def checkout(carts: List[ShoppingCart]): ShoppingCart = combineFold(carts)


  val carts = List(ShoppingCart(List("pen"), 10), ShoppingCart(List("book"), 20))

  println(checkout(carts))

}
