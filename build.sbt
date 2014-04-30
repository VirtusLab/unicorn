val `unicorn-core` = project
  .settings(Settings.common: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    crossScalaVersions := Seq("2.10.4", "2.11.0")
  )

val `unicorn-play` = project
  .settings(Settings.common: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    libraryDependencies ++= Dependencies.play
  )
  .dependsOn(`unicorn-core` % Settings.alsoOnTest)

val unicorn = project
  .in(file("."))
  .aggregate(`unicorn-core`, `unicorn-play`)
  .settings(
    name := "unicorn"
  )