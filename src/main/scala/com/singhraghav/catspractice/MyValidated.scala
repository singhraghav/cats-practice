package com.singhraghav.catspractice

object MyValidated extends App {
  import cats.data.Validated
  object FormValidation {
    type FormValidation[T] = Validated[List[String], T]

    def validateForm(form: Map[String, String]): FormValidation[String] = {
      def missingAndEmptyStringValidation(fieldName: String): Validated[List[String], String] =
        Validated
          .fromOption(form.get(fieldName), List(s"$fieldName is Missing"))
          .ensure(List(s"$fieldName is empty String"))(name => name.nonEmpty)

      val nameValidation = missingAndEmptyStringValidation("name")

      val emailValidation =
        missingAndEmptyStringValidation("email")
          .ensure(List("Invalid email Address doesn't contain '@'"))(mail => mail.contains("@"))

      val passwordValidation = missingAndEmptyStringValidation("password")
        .ensure(List("Invalid Password: Length Of Password should be >= 10"))(pass => pass.length >= 10)

      nameValidation
        .combine(emailValidation)
        .combine(passwordValidation)
        .map(_ => "Success")
    }
  }

  val form = Map("password" -> "1234567890987", "email" -> "raghav@test.com", "name" -> "raghav")

  println(FormValidation.validateForm(form))

}
