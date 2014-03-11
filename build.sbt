organization := "org.virtuslab"

name := "unicorn"

version := "0.4.3-SNAPSHOT"

scalaVersion := "2.10.3"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += Resolver.typesafeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick" % "0.5.0.8",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "com.typesafe.play" %% "play-test" % "2.2.0" % "test",
  "com.h2database" % "h2" % "1.3.174" % "test"
)
