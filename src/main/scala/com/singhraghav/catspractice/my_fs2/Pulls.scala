package com.singhraghav.catspractice.my_fs2

import cats.effect.{IO, IOApp}
import fs2._

object Pulls extends IOApp.Simple {
  override def run: IO[Unit] = {
    val s = Stream(1, 2) ++ Stream(3) ++ Stream(4, 5)

    val outputPull: Pull[Pure, Int, Unit] = Pull.output1(1)

    IO.println(outputPull.stream.toList)

    val outputChunk: Pull[Pure, Int, Unit] = Pull.output(Chunk(1, 2, 3))

    IO.println(outputChunk.stream.toList)

    val donePull: Pull[Pure, Nothing, Unit] = Pull.done

    val purePull: Pull[Pure, Nothing, Int] = Pull.pure(5)

    val combined: Pull[Pure, Int, Unit] =
      for {
        _ <- Pull.output1(1)
        _ <- Pull.output(Chunk(4, 5, 6))
      } yield ()

    IO.println(combined.stream.toList)

    val toPull: Stream.ToPull[Pure, Int] = s.pull

    val echoPull: Pull[Pure, Int, Unit] = s.pull.echo

    val takePull: Pull[Pure, Int, Option[Stream[Pure, Int]]] = s.pull.take(3)

    val unconsedRange = s.pull.uncons

    IO.println("")

  }
}
