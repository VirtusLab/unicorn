package org.virtuslab.unicorn.ids

import org.scalatest._


trait BaseTest extends FlatSpecLike with Matchers

trait AppTest extends BaseTest with BeforeAndAfterEach {

  import TestUnicorn.simple._

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  val DB = TestUnicorn.profile.backend.Database.forURL(dbURL,driver=dbDriver)

  /**
   * Runs function in rolled-back transaction.
   *
   * @param func function to run in rolled-back transaction
   * @tparam A type returned by `f`
   * @return value returned from `f`
   */
  def rollback[A](func: Session => A): A = DB.withTransaction {
    session: Session =>
      val out = func(session)
      session.rollback()
      out
  }
}