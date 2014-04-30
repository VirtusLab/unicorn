package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver
import org.virtuslab.unicorn.ids.repositories.{JunctionRepositories, IdRepositories, Repositories}

/**
 * Created by Łukasz Dubiel on 29.04.14.
 */

trait Unicorn extends Identifiers
  with Tables
  with Repositories {
  self: JdbcDriver =>
  type IdCompanion[I <: BaseId]
}

trait UnicornCore extends Unicorn {
  self: JdbcDriver =>

  override type IdCompanion[I <: BaseId] = CoreCompanion[I]
}
