package org.virtuslab.unicorn.utils

import slick.dbio.DBIOAction
import slick.jdbc.JdbcBackend

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait Invoker {

  protected val queryTimeout = Duration.Inf

  def invokeAction[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E])(implicit session: JdbcBackend#Session): R = {
    val db = session.database
    val singleSessionDb = SingleSessionDb.createFor(session, db.executor)
    Await.result(singleSessionDb.run(action), queryTimeout)
  }

}
