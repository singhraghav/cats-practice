ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "cats-practice"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.0"
libraryDependencies += "dev.zio" %% "zio" % "2.0.13"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.7.0"
libraryDependencies += "co.fs2" %% "fs2-io" % "3.7.0"

val http4sVersion = "0.23.19"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.14.5",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.14.5"
)
libraryDependencies += "eu.timepit" %% "refined" % "0.10.3"

//
//val circeVersion = "0.14.1"
//
//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)



