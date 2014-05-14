import com.typesafe.sbt.SbtScalariform
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype

object Settings {

  val unicornVersion = "0.6.0-M5"

  val alsoOnTest = "compile->compile;test->test"

  // settings for ALL modules, including parent
  val core = Seq(
    organization := "org.virtuslab",
    version := unicornVersion,
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
  ) ++ Sonatype.sonatypeSettings

  // common settings for play and core modules
  val common = core ++ Seq(
    scalaVersion := "2.10.4",
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    parallelExecution in Test := false,
    incOptions := incOptions.value.withNameHashing(true),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint"
    )
  ) ++ SbtScalariform.scalariformSettings

}