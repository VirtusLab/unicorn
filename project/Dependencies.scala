import sbt._

object Dependencies {
  
  val mainCore: Seq[ModuleID] = Seq(
    "com.typesafe.slick" %% "slick" % "3.3.2",
    "joda-time" % "joda-time" % "2.10.3",
    "org.joda" % "joda-convert" % "2.2.1"
  )

  val testCore = Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "com.h2database" % "h2" % "1.4.199" % "test",
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"
  )

  val core: Seq[ModuleID] = mainCore ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "5.0.0"
  )

  val testPlay = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    "com.typesafe.play" %% "play-test" % "2.8.2" % "test"
  )

  val play = mainPlay ++ testPlay
}
