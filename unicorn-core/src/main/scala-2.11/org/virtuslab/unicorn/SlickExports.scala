package org.virtuslab.unicorn

import slick.profile.BasicProfile

/**
  * This remapping of types handle differences between Slick 3.1.x and 3.2.x.
  *
  * This is not really connected that much but what we do here is we group bind together:
  * - Scala 2.11.x with Slick 3.1.x
  * - Scala 2.12.x with Slick 3.2.x
  *
  * The only reason for this is that:
  * - Play doesn't support yet Scala 2.12
  * - play-slick doesn't support neither Scala 2.12 nor Slick 3.2
  *
  * So in theory we could have Slick 3.2.x for both Scala 2.11.x and 2.12.x but play-slick seems to be extremely slow
  * in adapting changes (including even things like accepting PRs). It may be worth to think if we really need play-slick at all.
  */
object SlickExports {
  type JdbcProfile = slick.driver.JdbcProfile
  type DatabaseConfig[P <: BasicProfile] = slick.backend.DatabaseConfig[P]
}
