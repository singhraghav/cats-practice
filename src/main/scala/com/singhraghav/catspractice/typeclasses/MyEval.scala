package com.singhraghav.catspractice.typeclasses

object MyEval extends App {

  import cats.Eval

  def defer[T](eval: => Eval[T]): Eval[T] = Eval.later(()).flatMap(_ => eval)

  def reverseEval[T](list: List[T]): Eval[List[T]] =
    if(list.isEmpty) Eval.now(list)
    else Eval.defer(reverseEval(list.tail).map{_ :+ list.head})

  defer(Eval.now{println("Hello"); 42}).value

  println(reverseEval(List(1,2,3)).value)

}
