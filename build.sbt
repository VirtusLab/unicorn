inThisBuild(List(
  organization := "org.virtuslab",
  homepage := Some(url("https://github.com/VirtusLab/unicorn")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "jsroka",
      "jsroka",
      "jsroka@virtuslab.com",
      url("https://virtuslab.com/")
    )
  )
))

val `unicorn-core` = project
  .settings(Settings.core *)
  .settings(
    libraryDependencies ++= Dependencies.core,
    // cannot be higher due to tests not able to reproduce abnormal DB behavior
    coverageMinimum := 100,
    Test / scalastyleConfig := file("scalastyle-test-config.xml"),
  )

val `unicorn-play` = project
  .settings(Settings.play *)
  .settings(
    libraryDependencies ++= Dependencies.core,
    libraryDependencies ++= Dependencies.play,
    coverageMinimum := 100,
    Test / scalastyleConfig := file("scalastyle-test-config.xml"),
    Test / javaOptions ++= Seq(
      //FIXME This option isn't passed into IntelliJ run configurations
      "--add-opens=java.base/java.lang=ALL-UNNAMED"
    ),
  )
  .dependsOn(`unicorn-core` % Settings.alsoOnTest)

val unicorn = project
  .in(file("."))
  .aggregate(`unicorn-core`, `unicorn-play`)
  .dependsOn(`unicorn-core`, `unicorn-play`)
  .settings(Settings.parent *)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name := "unicorn"
  )