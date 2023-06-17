package com.singhraghav.catspractice.myserver

import cats.data.{Kleisli, OptionT}
import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s._
import org.http4s._
import org.http4s.server._
import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Authorization

case class User(id: Long, name: String)

object BasicAuthDemo extends IOApp.Simple {

  private val routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "welcome" / user =>
        Ok(s"Welcome $user")
    }

  private val server: Resource[IO, Server] =
    EmberServerBuilder.default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(routes.orNotFound)
      .build

  //Request[IO] => IO[Either[String, User]]
  //Kliesli[IO, Request[IO], Either[String, User]]
  val basicAuthMethod: Kleisli[IO, Request[IO], Either[String, User]] = {
  Kleisli.apply[IO, Request[IO], Either[String, User]]{ request =>
      val authHeader = request.headers.get[Authorization]
      authHeader match {
        case None => IO(Left("UnAuthorized !!!"))
        case Some(Authorization(BasicCredentials(creds))) => IO(Right(User(1L, creds._1)))
        case Some(_) => IO(Left("No basic credentials !!!"))
      }
    }
  }

  val onFailure: AuthedRoutes[String, IO] = Kleisli { (req: AuthedRequest[IO, String]) =>
    OptionT.pure[IO](Response[IO](status = Status.Unauthorized))
  }

  //middleware
  // something sitting bwn server logic (meaning paths) and final response

  val userBasicAuthMiddleware: AuthMiddleware[IO, User] = AuthMiddleware(basicAuthMethod, onFailure)

  val basicAuthedRoutes = AuthedRoutes.of[User, IO] {
      case GET -> Root / "welcome" as user =>
        Ok(s"Welcome $user")
    }

  val basicAuthedServer =
    EmberServerBuilder.default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8081")
      .withHttpApp(userBasicAuthMiddleware(basicAuthedRoutes).orNotFound)
      .build

  override def run: IO[Unit] = basicAuthedServer.use(_ => IO.never).void

}
