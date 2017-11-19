name := "Scala.RemoteMonad"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "0.5"

val circeVersion = "0.9.0-M2"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")