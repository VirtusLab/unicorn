package org.virtuslab.unicorn

/**
 * Cake for unicorn-core.
 */
trait UnicornCore extends Unicorn {
  self: HasJdbcDriver =>

  override type IdCompanion[Id <: BaseId] = CoreCompanion[Id]
}
