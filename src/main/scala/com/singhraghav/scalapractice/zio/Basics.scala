package com.singhraghav.scalapractice.zio

import zio._

import scala.io.StdIn
object Basics extends ZIOAppDefault {

  val goShopping: Task[Unit] = ZIO.attempt(println("Going to grocery store from ZIO APP"))

  val goShoppingLater: ZIO[Any, Throwable, Unit] = goShopping.delay(3.second)

  val firstName: Task[String] = ZIO.attempt(StdIn.readLine("[ZIO] What is your first Name"))

  val lastName: Task[String] = ZIO.attempt(StdIn.readLine("[ZIO] What is your last Name"))

  val fullName: ZIO[Any, Throwable, String] = firstName.zipWith(lastName)((first, last) => s"$first $last")

  val collectionResult: ZIO[Any, Throwable, List[Any]] =
    ZIO.collectAll(List(goShopping, firstName, lastName))
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = goShoppingLater
}
