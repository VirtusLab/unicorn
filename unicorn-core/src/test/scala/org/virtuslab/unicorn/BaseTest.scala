package org.virtuslab.unicorn

import slick.driver.H2Driver.api._
import org.scalatest._

trait RollbackHelper[Underlying] {

  val unicorn: Unicorn[Underlying] with HasJdbcDriver

  import unicorn.driver.api._

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

trait LongTestUnicorn {
  lazy val unicorn = TestUnicorn
}

trait BaseTest[Underlying] extends FlatSpecLike with Matchers with BeforeAndAfterEach with RollbackHelper[Underlying] {

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  override lazy val DB = unicorn.driver.profile.backend.Database.forURL(dbURL, driver = dbDriver)
  //    override lazy val DB = Database.forURL(dbURL, driver = dbDriver).asInstanceOf[unicorn.driver.profile.backend.DatabaseDef]
}