ThisBuild / organization := "com.coursor"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "3.7.1"

lazy val core = project
  .in(file("."))
  .settings(
    name := "CoursorCore",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.13",
      "io.circe" %% "circe-generic" % "0.14.13",
      "io.circe" %% "circe-parser" % "0.14.13",
      "org.http4s" %% "http4s-circe" % "0.23.30",
      "org.http4s" %% "http4s-ember-client" % "0.23.30",
      "org.http4s" %% "http4s-ember-server" % "0.23.30",
      "org.typelevel" %% "cats-effect" % "3.6.1"
    )
  )
