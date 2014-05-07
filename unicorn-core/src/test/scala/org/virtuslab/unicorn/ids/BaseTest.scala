package org.virtuslab.unicorn.ids

import org.scalatest._

trait RollbackHelper {

  val unicorn: Unicorn with HasJdbcDriver

  import unicorn.driver.simple._

  def DB: unicorn.driver.profile.backend.DatabaseDef

  /**
   * Runs function in rolled-back transaction.
   *
   * @param func function to run in rolled-back transaction
   * @tparam Result type returned by `f`
   * @return value returned from `f`
   */
  def rollback[Result](func: Session => Result): Result = DB.withTransaction {
    session: Session =>
      val out = func(session)
      session.rollback()
      out
  }
}

trait BaseTest extends FlatSpecLike with Matchers with BeforeAndAfterEach with RollbackHelper {

  override lazy val unicorn = TestUnicorn

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  override val DB = unicorn.driver.profile.backend.Database.forURL(dbURL, driver = dbDriver)
}