Contributing
============

All kind of tips & tricks for people interested in contributing to unicorn.

CI job
------

A Travis job is set up for `unicorn` [here](https://travis-ci.org/VirtusLab/unicorn). Each branch and PR is cross-tested against `Scala 2.10.4`, `Scala 2.11.5`, `OpenJDK 7` and `OpenJDK 8`.

Code coverage
-------------

Unicorn uses [scoverage](https://github.com/scoverage/scalac-scoverage-plugin) plugin for code coverage. To run it, use:

```
sbt clean coverage test
```

(`clean` *is important*)

Results are placed in `unicorn\unicorn-core\target\scala-2.11\scoverage-report` and `unicorn\unicorn-play\target\scala-2.11\scoverage-report`.

Minimum coverage is set for both projects, *100%* for `unicorn-play` and *98%* for `unicorn-core` (there are some DB screw-ups that are hard to test there), so all code you add to project *have to be 100% test-covered*, otherwise Travis build will fail.

Releasing
---------

Before release you must have access to Sonatype and have PGP keys for signing artifacts.

To automate release process, `unicorn` uses [sbt-release](https://github.com/sbt/sbt-release) plugin. To release a new version, just use `sbt release` and follow instructions. For more information, see plugin docs.

**Warn** - You should *not* update version file (`version.sbt`) yourself, `sbt-release` does it for you.
