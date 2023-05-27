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




