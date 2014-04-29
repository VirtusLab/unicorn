import sbt._
import sbt.Keys._

object Unicorn extends Build {

  val defaultSettings = Seq(
    organization := "org.virtuslab",
    version := "0.5.0-SNAPSHOT",
    scalaVersion := "2.10.4",
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    parallelExecution in Test := false
  )

  val `unicorn-core` = project
    .settings(defaultSettings:_*)
    .settings(
      libraryDependencies ++= Dependencies.core
    )

  val `unicorn-play` = project
    .settings(defaultSettings:_*)
    .settings(
      libraryDependencies ++= Dependencies.core,
      libraryDependencies ++= Dependencies.play
    )
    .dependsOn(`unicorn-core`)

  val root = project.in(file(".")).aggregate(`unicorn-core`, `unicorn-play`).settings(
    name := "unicorn"
  )
}
