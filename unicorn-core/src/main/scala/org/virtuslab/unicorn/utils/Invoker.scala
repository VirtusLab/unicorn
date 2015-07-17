package org.virtuslab.unicorn.utils

import slick.dbio.DBIOAction
import slick.driver.JdbcDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait Invoker {

  protected val queryTimeout = Duration.Inf

  private[unicorn] def invokeAction[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])(implicit session: Session): R = {
    Await.result(session.database.run(action), queryTimeout)
  }
}
