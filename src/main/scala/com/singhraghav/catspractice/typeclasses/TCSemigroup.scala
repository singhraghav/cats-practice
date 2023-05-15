package com.singhraghav.catspractice.typeclasses

object TCSemigroup extends App {

  import cats.Semigroup
  import cats.syntax.semigroup._
  def reduceThings[T : Semigroup](things: List[T]): T = things.reduce(_ |+| _)
  case class Expense(id: Long, amount: Double)

  implicit val expenseSemiGroup: Semigroup[Expense] =
    Semigroup.instance((exp1, exp2) => Expense(id = Math.max(exp1.id, exp2.id), amount = exp1.amount + exp2.amount))

  val expenses = List(Expense(1, 1), Expense(2, 12))

  val expenseOption = List(Option(Expense(1, 1)), None)

  println(reduceThings(expenses))

  import cats.instances.option._
  println(reduceThings(expenseOption))

  // extension methods from semigroup


  val finalExpense = Expense(1, 2) |+| Expense(3, 10)

  println(finalExpense)


}
