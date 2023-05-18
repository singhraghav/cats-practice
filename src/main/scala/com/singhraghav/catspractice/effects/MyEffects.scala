package com.singhraghav.catspractice.effects

object MyEffects extends App {

  /*
  * The effect should describe what action will be performed
  * Effect should express what value it will generate
  * If there is a side effect the create of Effect should be separated from execution of side effect
  * */

  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))

    def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
  }

}
