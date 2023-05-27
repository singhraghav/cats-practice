package com.singhraghav.catspractice.exercises

import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.IOException
import scala.concurrent.{ExecutionContext, Future}
import scala.io.BufferedSource
import scala.util.Try

object ZioBasic extends ZIOAppDefault {

  def readFile(path: String): String = {
    val source: BufferedSource = scala.io.Source.fromFile(path)
    try source.getLines.mkString finally source.close()
  }

  // ZIO version

  def readFileZIO(path: String): Task[String] = ZIO.attempt(readFile(path))

  // with resource handling

  def readFileWithResourceHandling(path: String) = {
    val resource: ZIO[Scope, Throwable, BufferedSource] =
      ZIO.acquireRelease(ZIO.attempt(scala.io.Source.fromFile(path)))(source =>  ZIO.succeed(source.close()) *> Console.printLine("closing reader").orDie)

    ZIO.scoped(resource.flatMap(reader => ZIO.succeed(reader.getLines.mkString("\n"))))
  }.flatMap(content => Console.printLine(content))

  //example
//  val solution: ZIO[Any, Throwable, Unit] =
//    readFileWithResourceHandling("src/main/resources/example.txt").flatMap(content => Console.printLine(content))

  val random = ZIO.attempt(scala.util.Random.nextInt(3) + 1)
  def printLine(line: String) = ZIO.attempt(println(line))

  val readLine = ZIO.attempt(scala.io.StdIn.readLine())

  val solution2 = {
  for {
    randomNum <- random
    _         <- printLine("Guess a number from 1 to 3:")
    readNum   <- readLine
    _         <- if(readNum == randomNum.toString) printLine("You guessed right !") else printLine(s"You guessed wrong, the number was $randomNum")
  } yield ()

  }


  final case class MyZIO[-R, +E, +A](run: R => Either[E, A]) {

    def map[B](f: A => B): MyZIO[R, E, B] = MyZIO((r: R) => run(r).map(a => f(a)))

    def flatMap[R1 <: R, E1 >: E, B](f: A => MyZIO[R1, E1, B]): MyZIO[R1, E1, B] = MyZIO((r: R1) => run(r).flatMap(a => f(a).run(r)))

    def zipWith[R1 <: R , E1 >: E, A1 >: A, B, C](that: MyZIO[R1, E1, B])(f: (A1, B) => C): MyZIO[R1, E1, C] = (for {
      a <- this
      b <- that
    } yield (a, b)).map{case (a, b) => f(a, b)}

  }

  object MyZIO {
    def collectAll[R, E, A](in: Iterable[MyZIO[R, E, A]]): MyZIO[R, E, List[A]] = ???
  }

  val myZioTest1 = MyZIO((x: Int) => Right(x)).map(_ * 10)

  val zipWithTest = myZioTest1.zipWith(MyZIO((x: Int) => Right(x / 2)))((a, b) => a * b)


  println(zipWithTest.run(10))

  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] = either match {
    case Left(e) => ZIO.fail(e)
    case Right(v) => ZIO.succeed(v)
  }

  def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] = list.headOption.map(a => ZIO.succeed(a)).getOrElse(ZIO.fail(None))

  def getCacheValue(key: String, onSuccess: String => Unit, onFailure: Throwable => Unit): Unit = ???

  def getCacheValueZIO(key: String): Task[String] = ZIO.async { register =>
    getCacheValue(key, str => register(ZIO.succeed(str)), error => register(ZIO.fail(error)))
  }

  trait User

  def saveUserRecord(user: User, onSuccess: () => Unit, onFailure: Throwable => Unit): Unit = ???

  def saveUserRecordZIO(user: User): ZIO[Any, Throwable, Unit] = ZIO.async { register =>
    saveUserRecord(user, () => register(ZIO.unit), error => register(ZIO.fail(error)))
  }

  trait Query
  trait Result

  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] = ???

  def doQueryZIO(query: Query): Task[Result] = ZIO.fromFuture(implicit ec => doQuery(query))


  def readUntil(acceptInput: String => Boolean): ZIO[Any, Throwable, String] = (for {
    input <- readLine
    _     <- printLine(input)
  } yield input).repeatUntil(acceptInput)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = readUntil(str => str.equals("raghav"))

  // page number 60
}

