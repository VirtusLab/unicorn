package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.Repositories
import slick.jdbc.JdbcProfile

trait HasJdbcProfile {

  val profile: JdbcProfile

}

/**
 * Base cake for Unicorn. Extended by versions for `unicorn-core` and `unicorn-play`.
 */
trait Unicorn[Underlying]
  extends Tables[Underlying]
  with Repositories[Underlying] {
  self: HasJdbcProfile =>
}
