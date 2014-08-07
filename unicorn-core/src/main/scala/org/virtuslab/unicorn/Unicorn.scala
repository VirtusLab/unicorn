package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.Repositories

import scala.language.higherKinds
import scala.slick.driver.JdbcDriver

trait HasJdbcDriver {
  val driver: JdbcDriver
}

/**
 * Base cake for Unicorn. Extended by versions for `unicorn-core` and `unicorn-play`.
 */
protected[unicorn] abstract class Unicorn[Underlying](implicit val ordering: Ordering[Underlying])
    extends Identifiers[Underlying]
    with Tables[Underlying]
    with Repositories[Underlying] {
  self: HasJdbcDriver =>

  /** Abstract type for companions for Ids*/
  type IdCompanion[Id <: BaseId]
}
