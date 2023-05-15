package com.singhraghav.catspractice.typeclasses

import java.util.concurrent.Executors
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object MonadTransformers extends App {

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))


  val bandwidths = Map("server1" -> 50, "server2" -> 5300, "server3" -> 170)

  import cats.data.EitherT
  import cats.instances.future._

  type AsyncResponse[T] = EitherT[Future, String, T]

  def getBandwidth(server: String): AsyncResponse[Int] = bandwidths.get(server) match {
    case Some(b) if b > 300 => EitherT.right(Future.failed(new Exception("some error")))
    case Some(b) => EitherT.right(Future(b))
    case None    => EitherT.left(Future(s"Server $server unreachable"))
  }

  def canWidthStandSurge(s1: String, s2: String): AsyncResponse[Boolean] = for{
    cap1 <- getBandwidth(s1)
    cap2 <- getBandwidth(s2)
  } yield cap1 + cap2 > 250


  canWidthStandSurge("server2", "server3").value.foreach(println)

  println(Await.result(canWidthStandSurge("server5", "server4").value, Duration.Inf))


}
