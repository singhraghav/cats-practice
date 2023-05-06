package com.singhraghav.catspractice

import scala.annotation.tailrec

object MyWriter extends App {

  import cats.data.Writer


  def countAndSay(n: Int): Unit = {
    if (n <= 0) println("starting")
    else {
      countAndSay(n-1)
      println(n)
    }
  }

  def countAndSayWithWriter(n: Int): Writer[List[String], Unit] = {
    val initialWriter: Writer[List[String], Unit] = Writer(Nil, ())

    @tailrec
    def recur(n: Int, writer: Writer[List[String], Unit]): Writer[List[String], Unit] =
      if(n <= 0)  writer.mapWritten("starting" :: _)
      else
        {
          recur(n - 1, writer.mapWritten(s"$n" :: _))
        }

    recur(n, initialWriter)
  }


  println(countAndSayWithWriter(10).written)


}
