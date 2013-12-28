package org.virtuslab.unicorn.ids

import org.scalatest.{BeforeAndAfterEach, FlatSpecLike, Matchers}
import play.api.Play
import play.api.db.slick.DB
import play.api.test.FakeApplication
import scala.slick.session.Session

trait BaseTest extends FlatSpecLike with Matchers

trait AppTest extends BaseTest with BeforeAndAfterEach {

  private val testDb = Map(
    "db.default.driver" -> "org.h2.Driver",
    "db.default.url" -> "jdbc:h2:mem:unicorn",
    "db.default.user" -> "sa",
    "db.default.password" -> ""
  )

  implicit val app = new FakeApplication(additionalConfiguration = testDb)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    Play.start(app)
  }

  override protected def afterEach(): Unit = {
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
