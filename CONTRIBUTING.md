Contributing
============

All kind of tips & tricks for people interested in contributing to unicorn.

Code coverage
-------------

Unicorn uses [scoverage](https://github.com/scoverage/scalac-scoverage-plugin) plugin for code coverage. To run it, use:

```
sbt clean scoverage:test
```

(`clean` *is important*)

Results are placed in `unicorn\unicorn-core\target\scala-2.10\scoverage-report` and `unicorn\unicorn-play\target\scala-2.10\scoverage-report`.

Releasing
---------

Before release you must have access to Sonatype and have PGP keys for signing artifacts. 
For make release just use `+publishSigned +releaseSonatype`. 
