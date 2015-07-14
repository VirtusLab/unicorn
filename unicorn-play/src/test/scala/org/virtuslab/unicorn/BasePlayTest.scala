package org.virtuslab.unicorn

import org.scalatest._
import play.api.Play
import play.api.test.FakeApplication

trait BasePlayTest
    extends FlatSpecLike
    with OptionValues
    with Matchers
    with BeforeAndAfterEach
    with RollbackHelper[Long]
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

  override lazy val unicorn: Unicorn[Long] with HasJdbcDriver = LongUnicornPlay

  override protected def beforeEach(data: TestData): Unit = {
    import scala.slick.jdbc.StaticQuery
    DB.withSession(session =>
      StaticQuery.queryNA[Int]("DROP ALL OBJECTS").execute(session)
    )
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    Play.stop(app)
    super.afterEach()
  }

  override def DB = play.api.db.slick.DB(app).asInstanceOf[unicorn.driver.profile.backend.DatabaseDef]
}