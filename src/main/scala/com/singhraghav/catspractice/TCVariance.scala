package com.singhraghav.catspractice

object TCVariance extends App {

  import cats.Eq
  import cats.instances.int._
  import cats.instances.option._
  import cats.syntax.eq._

  val aComparsion = Option(2) === Option(3)

  trait Animal
  case class Cat() extends Animal

  // Either make the type class covariant or contravariant

  //Contravariant
  trait Vet[-T]

  implicit object VetOfAnimal extends Vet[Animal]
// if this is present then compiler throws exception because both implicit qualify as Vet[Cat]
  //  implicit object VetOfCat extends Vet[Cat]

  def heal[T](implicit vet: Vet[T]): Unit = println(vet)

  // This works because the Vet class is contra variant so event though we need Vet[Cat]
  // but because of contravariance Vet[Animal] <: Vet[Cat]. hence implicit of Vet[Animal] is picked up
  heal[Cat]

  //contravariance
  trait Builder[+T]

//  implicit object AnimalBuilder extends Builder[Animal]
  implicit object CatBuilder extends Builder[Cat]
  def build[T](implicit builder: Builder[T]): Unit = println(builder)

//  build[Cat]
  // this works with Builder[Cat] because type class is contravariant hence Builder[Cat] <: Builder[Animal] (Builder of cat is also a builder of animal)
  build[Animal]

}
