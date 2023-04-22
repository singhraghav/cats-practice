package com.singhraghav.catspractice
import cats.Functor
object MyFunctor extends App {

  trait CustomFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: A => B): F[B]
  }

  trait Tree[+T]

  object Tree {

    implicit object TreeFunctor extends Functor[Tree] {
      override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
        case Branch(v, l, r) => Branch(f(v), map(l)(f), map(r)(f))
        case Leaf(v) => Leaf(f(v))
      }
    }
  }

  case class Leaf[+T](value: T) extends Tree[T]

  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]


  def do10X[F[_]](initial: F[Int])(implicit func: Functor[F]): F[Int] = func.map(initial)(_ * 10)

  val leaf10: Tree[Int] = Leaf(10)
  val branch: Tree[Int] = Branch(10, Leaf(5), Branch(20, leaf10, leaf10))

  println(do10X(branch))

  import cats.syntax.functor._

  println(branch.map(_* 5))

  implicit class Do10xOps[F[_]: Functor](wrapper: F[Int]){
    def do10X: F[Int] = wrapper.map(_ * 10)
  }

  println(branch.do10X)
}
