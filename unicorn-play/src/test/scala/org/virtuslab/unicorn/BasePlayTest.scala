package org.virtuslab.unicorn

import org.scalatest._
import play.api.Play
import play.api.test.FakeApplication
import slick.driver.H2Driver.api._

trait BasePlayTest
    extends FlatSpecLike
    with OptionValues
    with Matchers
    with BeforeAndAfterEach
    with BaseTest[Long]
    with BeforeAndAfterAll {

  private val testDb = Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:play",
    "slick.dbs.default.db.user" -> "sa",
    "slick.dbs.default.db.password" -> ""
  )

  implicit val app: FakeApplication = {
    val fake = new FakeApplication(additionalConfiguration = testDb)
    Play.start(fake)
    fake
  }

  override lazy val unicorn: Unicorn[Long] with HasJdbcDriver = LongUnicornPlay

  override protected def beforeEach(data: TestData): Unit = {
    DB.run(sqlu"""DROP ALL OBJECTS""")
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    Play.stop(app)
    super.afterEach()
  }

}