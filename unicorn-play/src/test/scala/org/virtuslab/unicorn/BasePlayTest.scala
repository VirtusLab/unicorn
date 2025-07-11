package org.virtuslab.unicorn

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.db.slick.DatabaseConfigProvider
import play.api.{ Application, Configuration, Play }
import play.api.inject.guice.GuiceApplicationBuilder
import org.scalatestplus.play.guice.GuiceFakeApplicationFactory
import slick.jdbc.JdbcProfile

trait BasePlayTest
  extends AnyFlatSpecLike
  with OptionValues
  with Matchers
  with BeforeAndAfterEach
  with BaseTest[Long]
  with BeforeAndAfterAll
  with GuiceFakeApplicationFactory {

  private val testDb = Configuration(
    "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:play",
    "slick.dbs.default.db.user" -> "sa",
    "slick.dbs.default.db.password" -> "")

  implicit val app: Application = {
    val fake = GuiceApplicationBuilder(configuration = testDb).build()
    Play.start(fake)
    fake
  }

  override lazy val unicorn: Unicorn[Long] with HasJdbcProfile = {
    val databaseConfigProvider = app.injector.instanceOf[DatabaseConfigProvider]
    new LongUnicornPlay(databaseConfigProvider.get[JdbcProfile])
  }

  import unicorn.profile.api._

  override protected def beforeEach(): Unit = {
    DB.run(sqlu"""DROP ALL OBJECTS""")
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    Play.stop(app)
    super.afterEach()
  }

}