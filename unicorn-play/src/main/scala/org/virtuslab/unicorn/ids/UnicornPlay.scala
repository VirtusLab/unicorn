package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver
import org.virtuslab.unicorn.ids.repositories.Repositories

/**
 * Created by Łukasz Dubiel on 30.04.14.
 */
trait UnicornPlay
  extends Identifiers
  with Tables
  with Repositories
  with PlayIdentifiers
  with Unicorn {
  self: JdbcDriver =>

  override type IdCompanion[I <: BaseId] = PlayCompanion[I]
}
