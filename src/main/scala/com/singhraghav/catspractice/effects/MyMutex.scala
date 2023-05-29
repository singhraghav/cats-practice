package com.singhraghav.catspractice.effects

import cats.effect.{Deferred, IO, IOApp, Ref}

import scala.concurrent.duration.DurationInt
import scala.util.Random
import com.singhraghav.catspractice.effects.utils._
import cats.syntax.parallel._

import scala.collection.immutable.Queue

abstract class MyMutex {

  def acquire: IO[Unit]

  def release: IO[Unit]

}
object MyMutex {

  case class State(locked: Boolean, waitingQueue: Queue[Deferred[IO, Unit]])

  val unlocked = State(locked = false, Queue())

  def createSignal: IO[Deferred[IO, Unit]] = Deferred[IO, Unit]
  def create: IO[MyMutex] = Ref[IO].of(unlocked).map { state =>
    new MyMutex {
      override def acquire: IO[Unit] = createSignal.flatMap{ signal =>
        state.modify {
          case State(false, _) => (State(locked = true, Queue()), IO.unit)
          case State(true, waitingQueue) => (State(locked = true, waitingQueue = waitingQueue.enqueue(signal)), signal.get)
        }.flatten
      }

      override def release: IO[Unit] = state.modify {
        case State(false, _) => (unlocked, IO.unit)
        case State(true, queue) =>
        if (queue.isEmpty) (unlocked, IO.unit)
        else {
          val (signal, rest) = queue.dequeue
          (State(locked = true, rest), signal.complete(()).void)
        }
      }.flatten
    }
  }

}

object MutexPlayground extends IOApp.Simple {

  def criticalTask(): IO[Int] = IO.sleep(1.second) >> IO(Random.nextInt(100))

  def createNonLockingTask(id: Int): IO[Int] = for {
    _ <- IO(s"[task $id] working ...").myDebug
    res <- criticalTask()
    _ <- IO(s"[task $id] got result: $res").myDebug
  } yield res

  def demoNonLockingTasks(): IO[List[Int]] = (1 to 10).toList.parTraverse(id => createNonLockingTask(id))

  def createLockingTask(id: Int, mutex: MyMutex) = for {
    _ <- IO(s"[task $id] waiting for permission ...").myDebug
    _ <- mutex.acquire // block if mutex is acquired by some other thread
    _ <- IO(s"[task $id] working ...").myDebug
    res <- criticalTask()
    _ <- IO(s"[task $id] got result: $res").myDebug
    _ <- mutex.release
    _ <- IO(s"[task $id] lock removed ...").myDebug
  } yield res

  def lockingTaskDemo(): IO[List[Int]] = for {
    mutex <- MyMutex.create
    results <- (1 to 10).toList.parTraverse(id => createLockingTask(id, mutex))
  } yield results
  override def run: IO[Unit] = lockingTaskDemo().myDebug.void
}
