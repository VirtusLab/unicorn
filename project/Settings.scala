import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.pgp.PgpKeys
import sbtrelease.ReleasePlugin.autoImport._
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype

object Settings {

  val scala_2_11 = "2.11.12"
  val scala_2_12 = "2.12.6"

  val alsoOnTest = "compile->compile;test->test"

  // settings for ALL modules, including parent
  val common = Seq(
    organization := "org.virtuslab",

    parallelExecution in Test := false,
    testOptions in Test += Tests.Argument("-oDF"),
    autoAPIMappings := true,

    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      pomExtra := <url>https://github.com/VirtusLab/unicorn</url>
      <licenses>
        <license>
          <name>Apache-style</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>https://github.com/VirtusLab/unicorn.git</url>
        <connection>scm:git:git@github.com:VirtusLab/unicorn.git</connection>
      </scm>
      <developers>
        <developer>
          <id>VirtusLab</id>
          <name>VirtusLab</name>
          <url>http://www.virtuslab.com/</url>
        </developer>
        <developer>
          <id>JerzyMuller</id>
          <name>Jerzy Müller</name>
          <url>https://github.com/Kwestor</url>
        </developer>
      </developers>
  ) ++
    Sonatype.sonatypeSettings

  val core = common ++ Seq(
    scalaVersion := scala_2_12,
    crossScalaVersions := Seq(scala_2_11, scala_2_12),
    releaseCrossBuild := true
  )

  val play = common ++ Seq(
    scalaVersion := scala_2_12,
    crossScalaVersions := Seq(scala_2_11, scala_2_12),
    releaseCrossBuild := true
  )

  // common settings for play and core modules
  val parent = common ++ Seq(
    scalaVersion := scala_2_12,
    crossScalaVersions := Seq(scala_2_11, scala_2_12),
    releaseCrossBuild := true,
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
