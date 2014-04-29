import sbt._

object Dependencies {

  val mainCore = Seq(
    "com.typesafe.slick" %% "slick" % "2.0.0",
    "joda-time" % "joda-time" % "2.1"
  )

  val testCore = Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "com.h2database" % "h2" % "1.3.175" % "tes1t"
  )

  val core = mainCore ++ testCore

  val mainPlay = Seq(
    "com.typesafe.play" %% "play-slick" % "0.6.0.1"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.2.2" % "test"
  )

  val play = mainPlay ++ testPlay

}