package org.virtuslab.unicorn

import scala.slick.lifted.MappedToBase

/**
 * Cake for unicorn-core.
 */
trait UnicornCore extends Unicorn {
  self: HasJdbcDriver =>

  override type IdCompanion[Id <: MappedToBase] = CoreCompanion[Id]
}
