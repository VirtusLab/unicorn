import scoverage.ScoverageSbtPlugin.ScoverageKeys

val `unicorn-core` = project
  .settings(Settings.common: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    // cannot be higher due to tests not able to reproduce abnormal DB behavior
    ScoverageKeys.coverageMinimum := 98
  )

val `unicorn-play` = project
  .settings(Settings.common: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    libraryDependencies ++= Dependencies.play,
    ScoverageKeys.coverageMinimum := 100
  )
  .dependsOn(`unicorn-core` % Settings.alsoOnTest)

val unicorn = project
  .in(file("."))
  .aggregate(`unicorn-core`, `unicorn-play`)
  .dependsOn(`unicorn-core`, `unicorn-play`)
  .settings(Settings.core: _*)
  .settings(
    name := "unicorn"
  )
