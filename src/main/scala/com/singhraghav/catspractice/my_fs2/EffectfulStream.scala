package com.singhraghav.catspractice.my_fs2

import fs2._
import cats.effect.{IO, IOApp}

object EffectfulStream extends IOApp.Simple {
  override def run: IO[Unit] = {
    val s: Stream[IO, Unit] = Stream.eval(IO.println("first effectful stream"))

//    s.compile.toList.flatMap( IO.println)

    s.compile.drain

    val s2: Stream[IO, Nothing] = Stream.exec(IO.println("second effectful stream"))

    s2.compile.drain

    val fromPure: Stream[Pure, Int] = Stream(1, 2, 3)

    val ioFromPure = fromPure.covary[IO]

    val natsEval = Stream.iterateEval(1)(v => IO(v + 1))

    natsEval.take(5).compile.toList.flatMap(IO.println)

    val alphabet = Stream.unfoldEval('a')(a => IO(if(a == 'z' + 1) None else Some((a, (a+ 1).toChar))))

    alphabet.compile.toList.flatMap(IO.println)

    val data = List.range(1, 100)

    val pageSize = 20

    def fetchPage(pageNumber: Int): IO[List[Int]] = {
      val start = pageNumber * pageSize
      val end = start + pageSize
      IO.println(s"Fetching page $pageNumber").as(data.slice(start, end))
    }

    def fetchAll(): Stream[IO, List[Int]] = {
      Stream.unfoldEval(0) { pageNum =>
        fetchPage(pageNum).map { pageElement =>
          if(pageElement.isEmpty) None
          else Some((Stream.emit(pageElement), pageNum + 1))
        }
      }.flatten
    }

    fetchAll().compile.toList.flatMap(IO.println)
  }
}
