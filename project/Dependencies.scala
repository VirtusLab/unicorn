import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "joda-time" % "joda-time" % "2.8.1",
    "org.joda" % "joda-convert" % "1.7"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.h2database" % "h2" % "1.4.187" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3" % "test"
  )

  def core(scalaVersion: String) = mainCore(scalaVersion) ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "3.0.0-M2"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.6.0-M1" % "test"
  )

  val play = mainPlay ++ testPlay
}
