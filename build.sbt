ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.12"

lazy val akkaVersion = "2.3.11"

lazy val root = (project in file("."))
  .settings(
    name := "AkkaTask"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.6",
  "org.jsoup" % "jsoup" % "1.14.2",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)