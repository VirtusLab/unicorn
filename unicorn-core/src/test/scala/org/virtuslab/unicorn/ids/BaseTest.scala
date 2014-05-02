package org.virtuslab.unicorn.ids

import org.scalatest._

trait BaseTest extends FlatSpecLike with Matchers with BeforeAndAfterEach {

  import TestUnicorn.driver.simple._

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  val DB = TestUnicorn.driver.profile.backend.Database.forURL(dbURL, driver = dbDriver)

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