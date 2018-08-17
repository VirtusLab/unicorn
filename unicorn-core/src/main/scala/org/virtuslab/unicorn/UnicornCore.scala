package org.virtuslab.unicorn

/**
 * Cake for unicorn-core.
 */
trait UnicornCoreLike[Underlying] extends Unicorn[Underlying] {
  self: HasJdbcProfile =>
}

abstract class UnicornCore[Underlying](implicit val ordering: Ordering[Underlying])
  extends UnicornCoreLike[Underlying] {
  self: HasJdbcProfile =>
}

trait LongUnicornCore extends UnicornCore[Long] {
  self: HasJdbcProfile =>
}
