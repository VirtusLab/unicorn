package org.virtuslab.unicorn

import org.scalatest._
import play.api.Play
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.FakeApplication
import org.virtuslab.unicorn.ids.{ UnicornPlay, RollbackHelper }

trait BasePlayTest extends FlatSpecLike with Matchers with BeforeAndAfterEach with RollbackHelper {

  override lazy val unicorn = UnicornPlay

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

  override def DB = play.api.db.slick.DB(app).asInstanceOf[unicorn.driver.profile.backend.DatabaseDef]
}