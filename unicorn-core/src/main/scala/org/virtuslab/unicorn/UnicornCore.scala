package org.virtuslab.unicorn

/**
 * Cake for unicorn-core.
 */
trait UnicornCore[Id] extends Unicorn[Id] {
  self: HasJdbcDriver =>

  override type IdCompanion[T <: BaseId] = CoreCompanion[T]
}

trait LongUnicornCore extends UnicornCore[Long] {
  self: HasJdbcDriver =>
}
