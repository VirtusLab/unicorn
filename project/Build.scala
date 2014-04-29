import sbt._
import sbt.Keys._

object Unicorn extends Build {

  scalaVersion := "2.10.4"

  resolvers ++= Seq("Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
                    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                    Resolver.typesafeRepo("releases"))

  val core = Project(
    id="unicorn-core",
    base=file("core"))
    .settings(libraryDependencies ++= Dependencies.core)

  val play = Project(
    id="unicorn-play",
    base=file("play"))
    .settings(libraryDependencies ++= Dependencies.play)
    .dependsOn(core)

  val root = project.in(file(".")).aggregate(core, play).settings(
    organization := "org.virtuslab",
    name := "unicorn",
    version := "0.5.0-SNAPSHOT"
  )

  parallelExecution in Test := false


}
