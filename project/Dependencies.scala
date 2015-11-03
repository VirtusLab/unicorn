import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    "com.typesafe.slick" %% "slick" % "3.0.3",
    "joda-time" % "joda-time" % "2.9",
    "org.joda" % "joda-convert" % "1.8"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.h2database" % "h2" % "1.4.187" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3" % "test"
  )

  def core(scalaVersion: String) = mainCore(scalaVersion) ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "1.0.1"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.4.3" % "test"
  )

  val play = mainPlay ++ testPlay

}