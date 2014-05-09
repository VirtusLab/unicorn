package org.virtuslab.unicorn

import org.scalatest._
import play.api.Play
import play.api.test.FakeApplication
import org.virtuslab.unicorn.ids.{ UnicornPlay, RollbackHelper }

trait BasePlayTest
    extends FlatSpecLike
    with Matchers
    with BeforeAndAfterEach
    with RollbackHelper
    with BeforeAndAfterAll {

  private val testDb = Map(
    "db.default.driver" -> "org.h2.Driver",
    "db.default.url" -> "jdbc:h2:mem:unicorn",
    "db.default.user" -> "sa",
    "db.default.password" -> ""
  )

  implicit val app: FakeApplication = {
    val fake = new FakeApplication(additionalConfiguration = testDb)
    Play.start(fake)
    fake
  }

  override lazy val unicorn = UnicornPlay

  override protected def beforeEach(data: TestData): Unit = {
    import scala.slick.jdbc.StaticQuery
    DB.withSession(session =>
      StaticQuery.queryNA[Int]("DROP ALL OBJECTS").execute()(session)
    )
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    Play.stop()
    super.afterEach()
  }

  override def DB = play.api.db.slick.DB(app).asInstanceOf[unicorn.driver.profile.backend.DatabaseDef]
}