/* ************ */
/* Code quality */

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

/* ------------------ */
/* Deploy and release */

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.8")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.1")

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")

/* ------------- */
/* Code coverage */

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.4")


