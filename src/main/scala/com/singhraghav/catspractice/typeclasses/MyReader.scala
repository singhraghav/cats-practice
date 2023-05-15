package com.singhraghav.catspractice.typeclasses

import cats.Id

object MyReader extends App {


  case class Configuration(emailReplyTo: String)

  case class EmailService(emailReplyTo: String) {
    def sendEmail(address: String, contents: String) = s"From: $emailReplyTo; to: $address >>> $contents"
  }

  def getOrderStatus(username: String) = "Shipped"

  import cats.data.Reader
  def emailUser(user: String): Id[String] = {
    Reader.apply[Configuration, EmailService](configuration => EmailService(configuration.emailReplyTo))
      .map(emailService => emailService.sendEmail(user, "someContent"))
      .run(Configuration("raghav"))
  }

  println(emailUser("jina"))

}
