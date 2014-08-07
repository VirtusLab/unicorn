package org.virtuslab.unicorn

/**
 * Cake for unicorn-core.
 */
abstract class UnicornCore[Underlying](implicit ordering: Ordering[Underlying]) extends Unicorn[Underlying] {
  self: HasJdbcDriver =>

  override type IdCompanion[Id <: BaseId] = CoreCompanion[Id]
}

trait LongUnicornCore extends UnicornCore[Long] {
  self: HasJdbcDriver =>
}
