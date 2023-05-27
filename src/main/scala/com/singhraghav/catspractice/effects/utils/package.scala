package com.singhraghav.catspractice.effects

import cats.effect.IO

import java.time.LocalDateTime

package object utils {

  implicit class IODebug[A](io: IO[A]) {
    def myDebug: IO[A] = for {
      a <- io
      t = Thread.currentThread().getName
      _ = println(s"${LocalDateTime.now()} - [$t] $a")
    } yield a
  }

}
