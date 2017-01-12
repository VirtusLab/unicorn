import sbt._

object Dependencies {

  def mainCore(scalaVersion: String) = Seq(
    "com.typesafe.slick" %% "slick" % pickVersion(scalaVersion, ver211 = "3.1.1", ver212 = "3.2.0-M2"),
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
    "com.typesafe.play" %% "play-slick" % "2.0.2"
  )

  val testPlay = Seq(
    "com.typesafe.play" %% "play-test" % "2.5.3" % "test"
  )

  val play = mainPlay ++ testPlay

  def pickVersion(scalaVersion: String, ver211: String, ver212: String) = CrossVersion.partialVersion(scalaVersion) match {
    case Some((_, 11)) => ver211
    case Some((_, 12)) => ver212
    case _ => throw new IllegalStateException(s"Unsupported version passsed: ${scalaVersion}")
  }
}
