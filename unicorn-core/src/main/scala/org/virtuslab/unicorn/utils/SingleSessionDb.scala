package org.virtuslab.unicorn.utils

import java.sql.{ Connection, DatabaseMetaData }

import slick.jdbc.{ JdbcBackend, JdbcDataSource }
import slick.util.AsyncExecutor

@deprecated("Shouldn't be used and must be removed in next version.", "0.7.2")
private[utils] object SingleSessionDb {

  def createFor(_session: JdbcBackend#Session, executor: AsyncExecutor): JdbcBackend#Database = {

    def dummyJdbcDataSource = new JdbcDataSource {
      /*
        NOTE it's safe because this method is used in slick.jdbc.JdbcBackend.BaseSession, which is created
        by slick.jdbc.JdbcBackend.DatabaseDef.createSession which we override
       */
      def createConnection() = ???

      def close() = ()
    }

    new JdbcBackend.DatabaseDef(dummyJdbcDataSource, executor) {
      override def createSession() = {
        //NOTE path-dependent vs. type-projection type mismatch workaround
        val _session2: JdbcBackend.SessionDef = _session.asInstanceOf[JdbcBackend.SessionDef]

        new JdbcBackend.SessionDef {

          override def database: JdbcBackend.DatabaseDef = _session2.database

          override def rollback(): Unit = _session2.rollback()

          @volatile
          override def capabilities: JdbcBackend.DatabaseCapabilities = _session2.capabilities

          override def metaData: DatabaseMetaData = _session2.metaData

          override def close(): Unit = {
            //NOTE prevent closing the session
          }

          override def withTransaction[T](f: => T): T = _session2.withTransaction(f)

          override def conn: Connection = _session2.conn

          def startInTransaction: Unit = ()
          def endInTransaction(f: => Unit): Unit = ()
        }
      }

    }
  }

}