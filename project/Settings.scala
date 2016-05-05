import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.pgp.PgpKeys
import sbtrelease.ReleasePlugin.autoImport._
import scoverage.ScoverageSbtPlugin._
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype

object Settings {

  val alsoOnTest = "compile->compile;test->test"

  // settings for ALL modules, including parent
  val core = Seq(
    organization := "org.virtuslab",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.10.4", scalaVersion.value),
    releaseCrossBuild := true,
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

  // common settings for play and core modules
  val common = core ++ Seq(
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    parallelExecution in Test := false,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint",
      "-Xfatal-warnings"
    ),
    updateOptions := updateOptions.value.withCachedResolution(true),
    ScoverageKeys.coverageFailOnMinimum := true
  ) ++ SbtScalariform.scalariformSettings
}
