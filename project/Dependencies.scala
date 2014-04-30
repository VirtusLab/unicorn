import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    if (scalaVersion == "2.11.0") "com.typesafe.slick" % "slick_2.11.0-RC4" % "2.1.0-M1"
    else "com.typesafe.slick" %% "slick" % "2.0.0",
    "joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.6"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "2.1.5" % "test",
    "com.h2database" % "h2" % "1.3.175" % "test"
  )

  def core(scalaVersion: String) = mainCore(scalaVersion) ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "0.6.0.1"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.2.2" % "test"
  )

  val play = mainPlay ++ testPlay

}