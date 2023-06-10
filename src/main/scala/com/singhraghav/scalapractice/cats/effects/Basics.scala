package com.singhraghav.scalapractice.cats.effects

import cats.effect._
import cats.implicits.{catsSyntaxTuple2Semigroupal, toTraverseOps}

import scala.concurrent.duration.DurationInt
import scala.io.StdIn
object Basics extends IOApp.Simple {

  val goingShopping: IO[Unit] = IO.delay(print("Going to grocery store from Cats Effect"))

  val goShoppingLater: IO[Unit] = goingShopping.delayBy(3.second)

  val firstName: IO[String] = IO(StdIn.readLine("[Cats] what is your first name"))

  val lastName: IO[String] = IO(StdIn.readLine("[Cats] what is your last name"))

  val fullName: IO[String] = (firstName, lastName) mapN {
    case (first, last) => s"$first $last"
  }

  val collectAllEquivalent: IO[List[Any]] = List(goingShopping, firstName, lastName).sequence

  def getUserByIdAsync(id: Int)(cb: Option[String] => Unit): Unit = ???

  def getUserByIdAsyncIO(id: Int) = IO.async_ { cb: (Either[Throwable, String] => Unit) =>
    getUserByIdAsync(id){
      case Some(value) => cb(Right(value))
      case None => cb(Right("no user found"))
    }
  }
  override def run: IO[Unit] = goShoppingLater
}
