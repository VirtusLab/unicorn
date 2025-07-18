import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._

object Settings {

  val scala_2_12 = "2.12.20"
  val scala_2_13 = "2.13.16"

  val alsoOnTest = "compile->compile;test->test"

  // settings for ALL modules, including parent
  val common = Seq(
    organization := "org.virtuslab",
    scalaVersion := scala_2_13,
    crossScalaVersions := Seq(scala_2_12, scala_2_13),
    releaseCrossBuild := true,

    Test / fork := true,
    Test / parallelExecution := false,
    Test / testOptions += Tests.Argument("-oDF"),
    autoAPIMappings := true,
    scalacOptions += "-deprecation"
  )

  val core = common

  val play = common

  // common settings for play and core modules
  val parent = common ++ Seq(
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint",
      "-Xfatal-warnings"
    ),
    updateOptions := updateOptions.value.withCachedResolution(true),
    scoverage.ScoverageKeys.coverageFailOnMinimum := true
  )
}
