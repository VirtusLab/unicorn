import sbt._

object Dependencies {

  val mainCore: Seq[ModuleID] = Seq(
    "com.typesafe.slick" %% "slick" % "3.4.1",
    "joda-time" % "joda-time" % "2.14.0",
    "org.joda" % "joda-convert" % "3.0.1"
  )

  val testCore: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    "com.h2database" % "h2" % "2.3.232" % "test",
    "ch.qos.logback" % "logback-classic" % "1.5.18" % "test"
  )

  val core: Seq[ModuleID] = mainCore ++ testCore

  val mainPlay: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-slick" % "5.1.0"
  )

  val testPlay: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    "com.typesafe.play" %% "play-test" % "2.8.20" % "test"
  )

  val play: Seq[ModuleID] = mainPlay ++ testPlay
}
