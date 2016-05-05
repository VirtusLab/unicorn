import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    "com.typesafe.slick" %% "slick" % "3.1.0",
    "joda-time" % "joda-time" % "2.8.1",
    "org.joda" % "joda-convert" % "1.7"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.h2database" % "h2" % "1.4.187" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.3" % "test"
  )

  def core(scalaVersion: String) = mainCore(scalaVersion) ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "2.0.0"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.5.2" % "test"
  )

  val play = mainPlay ++ testPlay

}
