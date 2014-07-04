package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.Repositories
import scala.slick.driver.JdbcDriver

import scala.language.higherKinds
import scala.slick.lifted.MappedToBase

trait HasJdbcDriver {
  val driver: JdbcDriver
}

/**
 * Base cake for Unicorn. Extended by versions for `unicorn-core` and `unicorn-play`.
 */
protected[unicorn] trait Unicorn
    extends Identifiers
    with Tables
    with Repositories {
  self: HasJdbcDriver =>

  /** Abstract type for companions for Ids*/
  type IdCompanion[Id <: MappedToBase]
}
