package com.singhraghav.catspractice.my_fs2

import cats.effect.{IO, IOApp}
import fs2._

object Chunks extends IOApp.Simple {


  override def run: IO[Unit] = {
    val s1 = Stream(1, 2, 3)
    IO.println(s1.chunks.toList)

    val s2 = Stream(s1, Stream(4, 5, 6)).flatten

    IO.println(s2.chunks.toList)

    val s3 = Stream(1, 2) ++ Stream(3, 4) ++ Stream(5, 6)
    IO.println(s3.chunks.toList)

    val s4 = Stream.repeatEval(IO(42)).take(5)
    s4.chunks.compile.toList.flatMap(IO.println)

    val c: Chunk[Int] = Chunk(1, 2, 3)
    IO.println(c)

    val c2: Chunk[Int] = Chunk.array(Array(1 , 2, 3 , 4))
    IO.println(c2)

  }
}
