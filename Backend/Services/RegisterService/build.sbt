organization := "com.coursor"
name := "RegisterService"
version := "0.1.0"
scalaVersion := "3.7.1"

dependsOn(RootProject(file("../../Core")))

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.13",
  "io.circe" %% "circe-generic" % "0.14.13",
  "io.circe" %% "circe-parser" % "0.14.13",
  "org.typelevel" %% "cats-effect" % "3.6.1"
)
