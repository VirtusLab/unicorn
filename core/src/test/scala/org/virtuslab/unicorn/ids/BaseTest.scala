package org.virtuslab.unicorn.ids

import org.scalatest._
import play.api.Play
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test.FakeApplication

trait BaseTest extends FlatSpecLike with Matchers

trait AppTest extends BaseTest with BeforeAndAfterEach {

  private val testDb = Map(
    "db.default.driver" -> "org.h2.Driver",
    "db.default.url" -> "jdbc:h2:mem:unicorn",
    "db.default.user" -> "sa",
    "db.default.password" -> ""
  )

  implicit var app: FakeApplication = _

  override protected def beforeEach(data: TestData): Unit = {
    app = new FakeApplication(additionalConfiguration = testDb)
    Play.start(app)
    super.beforeEach()
  }

  override protected def afterEach(data: TestData): Unit = {
    Play.stop()
    super.afterEach()
  }

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
