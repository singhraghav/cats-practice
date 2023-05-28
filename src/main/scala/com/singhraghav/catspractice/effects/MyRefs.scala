package com.singhraghav.catspractice.effects

import cats.effect.{IO, IOApp, Ref}
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}

object MyRefs extends IOApp.Simple {

  import com.singhraghav.catspractice.effects.utils._

  val atomicMol: IO[Ref[IO, Int]] = Ref[IO].of(42)
  val atomicMol_v2: IO[Ref[IO, Int]] = IO.ref(42)

  val increasedMol = atomicMol.flatMap(ref => ref.set(43))

  val mol: IO[Int] = atomicMol.flatMap(ref => ref.get)

  val getSetMol = atomicMol.flatMap(ref => ref.getAndSet(43))

  val updatingWithFunction = atomicMol.flatMap{ ref =>
    ref.update(value => value * 10)
  }

  def demoConcurrentWorkPure(): IO[Unit] = {
    import cats.syntax.parallel._
    def task(workLoad: String, total: Ref[IO, Int]): IO[Unit] = {
      val wordCount = workLoad.split(" ").length
      for {
        _ <- IO(s"Counting words for '$workLoad': $wordCount").myDebug
        newCount <- total.updateAndGet(currentCount => currentCount + wordCount)
        _ <- IO(s"New total: $newCount").myDebug
      } yield ()
    }

    for {
      initialRef <- IO.ref(0)
      _ <- List("I love cats effect", "this ref is useless", "I write a lot of code").map(str => task(str, initialRef)).parSequence
    } yield ()
  }
  override def run: IO[Unit] = demoConcurrentWorkPure()
}
