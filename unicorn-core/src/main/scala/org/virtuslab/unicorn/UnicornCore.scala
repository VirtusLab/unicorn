package org.virtuslab.unicorn

/**
 * Cake for unicorn-core.
 */
trait UnicornCoreLike[Underlying] extends Unicorn[Underlying] {
  self: HasJdbcDriver =>

  override type IdCompanion[Id <: BaseId] = CoreCompanion[Id]
}

abstract class UnicornCore[Underlying](implicit val ordering: Ordering[Underlying])
    extends UnicornCoreLike[Underlying] {
  self: HasJdbcDriver =>
}

trait LongUnicornCore extends UnicornCore[Long] {
  self: HasJdbcDriver =>
}
