import com.typesafe.sbt.SbtScalariform
import scoverage.ScoverageSbtPlugin._
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype

object Settings {

  val unicornVersion = "0.6.2-SNAPSHOT"

  val alsoOnTest = "compile->compile;test->test"

  // settings for ALL modules, including parent
  val core = Seq(
    organization := "org.virtuslab",
    version := unicornVersion,
    scalaVersion := "2.11.2",
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
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
    Sonatype.sonatypeSettings ++
    instrumentSettings

  // common settings for play and core modules
  val common = core ++ Seq(
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    parallelExecution in Test := false,
    parallelExecution in  ScoverageTest := false,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint"
    )
  ) ++ SbtScalariform.scalariformSettings
}
