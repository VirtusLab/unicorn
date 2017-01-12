
val `unicorn-core` = project
  .settings(Settings.core: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    // cannot be higher due to tests not able to reproduce abnormal DB behavior
    coverageMinimum := 100,
    (scalastyleConfig in Test) := file("scalastyle-test-config.xml")
  )

val `unicorn-play` = project
  .settings(Settings.play: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    libraryDependencies ++= Dependencies.play,
    coverageMinimum := 100,
    (scalastyleConfig in Test) := file("scalastyle-test-config.xml")
  )
  .dependsOn(`unicorn-core` % Settings.alsoOnTest)

val unicorn = project
  .in(file("."))
  .aggregate(`unicorn-core`, `unicorn-play`)
  .dependsOn(`unicorn-core`, `unicorn-play`)
  .settings(Settings.parent: _*)
  .settings(unidocSettings: _*)
  .settings(
    name := "unicorn"
  )
