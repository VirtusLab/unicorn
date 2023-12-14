import sbt._

object Dependencies {

  val mainCore: Seq[ModuleID] = Seq(
    "com.typesafe.slick" %% "slick" % "3.4.1",
    "joda-time" % "joda-time" % "2.12.5",
    "org.joda" % "joda-convert" % "2.2.3"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    "com.h2database" % "h2" % "2.2.220" % "test",
    "ch.qos.logback" % "logback-classic" % "1.4.8" % "test"
  )

  val core: Seq[ModuleID] = mainCore ++ testCore

  val mainPlay = Seq(
    "org.playframework" %% "play-slick" % "6.0.0"
  )

  val testPlay = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    "com.typesafe.play" %% "play-test" % "2.8.20" % "test"
  )

  val play = mainPlay ++ testPlay
}
