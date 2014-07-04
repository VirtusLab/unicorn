val `unicorn-core` = project
  .settings(Settings.common: _*)
  .settings(
    libraryDependencies ++= Dependencies.core(scalaVersion.value)
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
  .dependsOn(`unicorn-core`, `unicorn-play`)
  .settings(Settings.core: _*)
  .settings(
    name := "unicorn"
  )
