package com.singhraghav.catspractice.myserver

import cats.data.{Kleisli, OptionT}
import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s._
import com.singhraghav.catspractice.myserver.HttpDigestDemo.middleware
import org.http4s._
import org.http4s.server._
import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Cookie
import org.http4s.server.middleware.authentication.DigestAuth
import org.http4s.server.middleware.authentication.DigestAuth.Md5HashedAuthStore

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.Base64
import scala.util.Try
object SessionDemo extends IOApp.Simple {

  val searchFunction: String => IO[Option[(User, String)]] = {
    case "raghav" =>
      for {
        user <- IO.pure(User(2L, "raghav"))
        hash <- Md5HashedAuthStore.precomputeHash[IO]("raghav", "http://localhost:8081", "12345")
        _ <- IO.println(hash)
      } yield Some((user, hash))

    case _ => IO.pure(None)
  }

  val authStore: DigestAuth.AuthStore[IO, User] = Md5HashedAuthStore(searchFunction)

  val middleware: IO[AuthMiddleware[IO, User]] = DigestAuth.applyF[IO, User](
    "http://localhost:8081",
    authStore
  )

  def today: String = LocalDateTime.now().toString
  def setToken(user: String, date: String) =
    Base64.getEncoder.encodeToString(s"$user:$date".getBytes(StandardCharsets.UTF_8))

  def getUser(token: String): Option[String] =
    Try(String.valueOf(Base64.getDecoder.decode(token)).split(":")(0)).toOption

  val basicAuthedRoutes = AuthedRoutes.of[User, IO] {
    case GET -> Root / "welcome" as user =>
      Ok(s"Welcome $user")
        .map(_.addCookie(ResponseCookie("sessioncookie", setToken(user.name, today), maxAge = Some(24 * 36000))))
  }


  val middleWareResource: Resource[IO, AuthMiddleware[IO, User]] = Resource.eval(middleware)

  val cookieAccessRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "statement" / user =>
      Ok(s"Here is the statement for $user")
    case GET -> Root / "logout" =>
    Ok("Logging out").map(_.removeCookie("sessioncookie"))
  }

  def checkSessionCookie(cookie: Cookie): Option[RequestCookie] =
    cookie.values.toList.find(_.name == "sessioncookie")

  def modifyPath(user: String): Path =
    Uri.Path.unsafeFromString(s"/statement/$user")
  def cookieCheckerApp(app: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { req =>
    val authHeader: Option[Cookie] = req.headers.get[Cookie]

    val result: OptionT[IO, Response[IO]] =
        OptionT.liftF(
          authHeader.fold(Ok("No cookies")) { cookie =>
            checkSessionCookie(cookie).fold(Ok("No Token")) { token =>
              getUser(token.content).fold(Ok("Invalid Token")) { user =>
                app.orNotFound.run(req.withPathInfo(modifyPath(user)))
              }
            }
          }
        )

    result
  }

  val routerResource: Resource[IO, HttpRoutes[IO]] = middleWareResource.map { mw =>
    Router(
      "/login" -> mw(basicAuthedRoutes),
      "/"      -> cookieCheckerApp(cookieAccessRoutes)
    )
  }


  val server =
    for {
      router <- routerResource
      server <- EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8081")
        .withHttpApp(router.orNotFound)
        .build
    } yield server

  override def run: IO[Unit] = server.use(_ => IO.never).void
}
