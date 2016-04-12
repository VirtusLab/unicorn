package org.virtuslab.unicorn.utils

import slick.dbio.DBIOAction
import slick.jdbc.JdbcBackend

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration

@deprecated("Shouldn't be used and must be removed in next version.", "0.7.2")
trait Invoker {

  protected val queryTimeout = Duration.Inf

  def invokeAction[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])(implicit session: JdbcBackend#Session): R = {
    val db = session.database
    val singleSessionDb = SingleSessionDb.createFor(session, db.executor)
    Await.result(singleSessionDb.run(action), queryTimeout)
  }

  def invokeActionAsync[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](
    action: DBIOAction[R, S, E]
  )(implicit session: JdbcBackend#Session): Future[R] = {
    val db = session.database
    val singleSessionDb = SingleSessionDb.createFor(session, db.executor)
    singleSessionDb.run(action)
  }

}
