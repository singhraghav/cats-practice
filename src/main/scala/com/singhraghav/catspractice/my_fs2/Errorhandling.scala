package com.singhraghav.catspractice.my_fs2

import cats.effect.{IO, IOApp}
import fs2._

object Errorhandling extends IOApp.Simple {
  override def run: IO[Unit] = {
    val s = Stream.eval(IO.raiseError(new Exception("some error")))
    val s2 = Stream.raiseError[IO](new Exception("some error 2"))

    val s3: Stream[IO, Int] = Stream.repeatEval(IO.println("emitting").as(42)).take(3) ++ s2

    val s4: Stream[IO, Int] = s3 ++ Stream.eval(IO.println("final emit").as(42))
    s4.compile.drain

    def doWork(i: Int): Stream[IO, Int] =
      Stream.eval(IO(math.random())).flatMap{ flag =>
        if(flag < 0.8) Stream.eval(IO.println(s"Processing $i").as(i))
        else Stream.raiseError[IO](new Exception(s"error while processing stream $i"))
      }

    val s5 = Stream.iterate(1)(_ + 1)
      .flatMap(doWork)


    s5.take(10)
//      .attempt
      .handleErrorWith(e => Stream.exec(IO.println(s"Recovering from error ${e.getMessage}")) )
      .compile.drain


    val handledStream = Stream(1, 2, 3, 4, 5, 6)
      .evalMap { num =>
        (if(num % 2 ==0)
            IO.raiseError(new Exception(s"Even number $num encountered"))
          else
            IO.pure(num))
          .handleErrorWith(error => IO.println(s"Error occurred: ${error.getMessage}").as(-1))
      }
      .handleErrorWith { error =>
        Stream.eval(IO.println(s"Error occurred: ${error.getMessage}")) >> Stream.empty
      }

    handledStream.compile.toList.flatMap(IO.println)


  }
}
