package com.singhraghav.catspractice


object CatsIntro extends App {

  //part 1 - get the type class by doing import
  import cats.Eq

  //get some instances of that type class
  import cats.instances.int._

  //part 3 - use type class API

  val intEquality = Eq[Int]

  val typeSafeComparison = intEquality.eqv(2, 3) //false
//  val unsafeComparison = intEquality.eqv(2, "two") doesn't compile

  //part 4 - use extension methods
  import cats.syntax.eq._
  val anotherTypeSafeComparison = 2 === 3
  val notSame = 2 =!= 3

  // extending to composite type
  import cats.instances.list._
  val aListComparion = List(1, 2) === List(2, 3)

  //part 6 - create new instance for custom type
  case class ToyCar(model: String, price: Double)
  import cats.instances.double._
  implicit val toyCarEq: Eq[ToyCar] = Eq.instance[ToyCar]((car1, car2) => car1.price === car2.price)

  println(ToyCar("model1", 123) === ToyCar("model2", 123))

}
