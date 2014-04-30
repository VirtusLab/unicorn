package org.virtuslab.unicorn.ids

import org.virtuslab.unicorn.ids.repositories.Repositories
import scala.slick.driver.JdbcDriver

import scala.language.higherKinds

trait Unicorn
  extends Identifiers
  with Tables
  with Repositories {
  self: JdbcDriver =>

  type IdCompanion[Id <: BaseId]
}

trait UnicornCore extends Unicorn {
  self: JdbcDriver =>

  override type IdCompanion[Id <: BaseId] = CoreCompanion[Id]
}
