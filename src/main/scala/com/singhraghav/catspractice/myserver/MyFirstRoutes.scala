package com.singhraghav.catspractice.myserver

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}
object MyFirstRoutes extends IOApp.Simple {

  private val helloWorldRoutes = HttpRoutes.of[IO] {
    case GET ->  "hello" /: name => Ok(s"Hello, $name")
    case GET ->  Root / "roll-num" / IntVar(rollNum) => Ok(s"Hello, $rollNum")
    case GET -> Root / file ~ "json" => Ok(s"""{"response": "You asked for $file"}""")
  }

  private val app = Router("/" -> helloWorldRoutes).orNotFound

  private val server: Resource[IO, Server] = EmberServerBuilder
    .default[IO]
    .withPort(port"8081")
    .withHttpApp(app)
    .build

  override def run: IO[Unit] = server.use(server => IO.println(s"server started on ${server.addressIp4s}") >> IO.never).as(ExitCode.Success)

}
