package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver
import org.virtuslab.unicorn.ids.repositories.{JunctionRepositories, IdRepositories, Repositories}

/**
 * Created by Łukasz Dubiel on 29.04.14.
 */
trait UnicornCore
  extends Identifiers
  with Tables
  with Repositories
  with IdRepositories
  with JunctionRepositories {
  self: JdbcDriver =>

}
