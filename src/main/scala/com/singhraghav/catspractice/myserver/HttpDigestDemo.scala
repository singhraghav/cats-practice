package com.singhraghav.catspractice.myserver

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s._
import org.http4s._
import org.http4s.server._
import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.authentication.DigestAuth
import org.http4s.server.middleware.authentication.DigestAuth.Md5HashedAuthStore

// digest authentication
// hash of user name, password realm
object HttpDigestDemo extends IOApp.Simple {


  // digest authentication
  // hash of user name, password realm

  val searchFunction: String => IO[Option[(User, String)]] = {
    case "raghav" =>
      for {
        user <- IO.pure(User(2L, "raghav"))
        hash <- Md5HashedAuthStore.precomputeHash[IO]("raghav", "http://localhost:8081", "12345")
        _    <- IO.println(hash)
      } yield Some((user, hash))

    case _ => IO.pure(None)
  }

  val authStore: DigestAuth.AuthStore[IO, User] = Md5HashedAuthStore(searchFunction)

  val middleware: IO[AuthMiddleware[IO, User]] = DigestAuth.applyF[IO, User](
    "http://localhost:8081",
    authStore
  )

  val basicAuthedRoutes = AuthedRoutes.of[User, IO] {
    case GET -> Root / "welcome" as user =>
      Ok(s"Welcome $user")
  }

  val middleWareResource: Resource[IO, AuthMiddleware[IO, User]] = Resource.eval(middleware)


  val server =
    for {
      middleWare <- middleWareResource
      server <- EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8081")
        .withHttpApp(middleWare(basicAuthedRoutes).orNotFound)
        .build
    } yield server

  override def run: IO[Unit] = server.use(_ => IO.never).void

}

