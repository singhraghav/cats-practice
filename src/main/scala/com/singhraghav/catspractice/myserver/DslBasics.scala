package com.singhraghav.catspractice.myserver

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
object DslBasics extends IOApp.Simple {

  private val globalRoute = HttpRoutes.of[IO] {
    case _ => Ok("everything ok ...").map(_.addCookie(ResponseCookie("foo", "bar")))
  }

  private val getRoot = Request[IO](Method.GET, uri"/")

  private val response1: IO[Response[IO]] = globalRoute.orNotFound.run(getRoot)
  override def run: IO[Unit] = globalRoute.orNotFound.run(getRoot).flatMap(res => IO.println(res.headers))
}
