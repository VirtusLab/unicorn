package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver

trait UnicornPlay
  extends PlayIdentifiers
  with Unicorn {
  self: JdbcDriver =>

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]
}
