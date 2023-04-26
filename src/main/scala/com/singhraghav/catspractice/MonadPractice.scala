package com.singhraghav.catspractice

import cats.syntax.flatMap._
import cats.syntax.functor._

object MonadPractice extends App {

  import cats.Monad

  import cats.instances.either._

  type ErrorOr[T] = Either[String, T]
  val eitherMonad = Monad[ErrorOr]

  case class OrderStatus(orderId: Long, status: String)

  case class Connection(host: String, port: String)

  val config = Map("host" -> "localhost", "port" -> "4040")

  trait HttpService[M[_]] {
    def getConnection(cfg: Map[String, String]): M[Connection]
    def issueRequest(connection: Connection, payload: String): M[String]
  }

  object OptionHttpService extends HttpService[Option] {
    override def getConnection(cfg: Map[String, String]): Option[Connection] = for {
      host <- cfg.get("host")
      port <- cfg.get("port")
    } yield Connection(host, port)

    override def issueRequest(connection: Connection, payload: String): Option[String] =
      if (payload.length < 20) Option(s"request $payload has been accepted") else None
  }

  object ErrorOrHttpService extends HttpService[ErrorOr] {
    override def getConnection(cfg: Map[String, String]): ErrorOr[Connection] = {
      val connection =
        for {
          host <- cfg.get("host")
          port <- cfg.get("port")
        } yield Connection(host, port)

      connection.map(Right(_)).getOrElse(Left("Connection parameters not found in config map"))
    }

    override def issueRequest(connection: Connection, payload: String): ErrorOr[String] =
      if (payload.length < 20) Right(s"request $payload has been accepted") else Left(s"Invalid payload $payload")
  }

  def getResponse[M[_]: Monad](httpService: HttpService[M], payload: String, config: Map[String, String]): M[String] = {
    for {
      conn     <- httpService.getConnection(config)
      response <- httpService.issueRequest(conn, payload)
    } yield response
  }
}
