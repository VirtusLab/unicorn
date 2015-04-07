import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    "com.typesafe.slick" %% "slick" % "2.1.0",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.h2database" % "h2" % "1.4.186" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.2" % "test"
  )

  def core(scalaVersion: String) = mainCore(scalaVersion) ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "0.9.0-M4"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.4.0-M3" % "test"
  )

  val play = mainPlay ++ testPlay

}