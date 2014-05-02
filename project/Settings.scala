import com.typesafe.sbt.SbtScalariform
import sbt.Keys._
import sbt._

object Settings {

  val alsoOnTest = "compile->compile;test->test"

  val common = Seq(
    organization := "org.virtuslab",
    version := "0.5.0-SNAPSHOT",
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