package org.virtuslab.unicorn

import org.virtuslab.unicorn.utils.Invoker
import slick.driver.JdbcDriver
import slick.lifted.Rep

/**
 * Class used to allow .list and .run syntax as in Slick < 3.0.0 via implicit conversion
 * @param rep Rep[T]
 * @param driver needed for Session
 */
class SyncRep[T](rep: Rep[T], driver: JdbcDriver) extends Invoker {

  import driver.api._

  final def list(implicit session: Session) = {
    invokeAction(rep.result)
  }

  final def run(implicit session: Session) = {
    invokeAction(rep.result)
  }

}
