package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.Repositories

import language.higherKinds
import slick.driver.JdbcDriver

trait HasJdbcDriver {
  val driver: JdbcDriver
}

/**
 * Base cake for Unicorn. Extended by versions for `unicorn-core` and `unicorn-play`.
 */
trait Unicorn[Underlying]
    extends Identifiers[Underlying]
    with Tables[Underlying]
    with Repositories[Underlying] {
  self: HasJdbcDriver =>

  def ordering: Ordering[Underlying]

  /** Abstract type for companions for Ids*/
  type IdCompanion[Id <: BaseId]
}
