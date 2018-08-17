package org.virtuslab.unicorn

import org.scalatest._
import play.api.db.slick.DatabaseConfigProvider
import play.api.{ Environment, Play }
import play.api.inject.guice.GuiceApplicationBuilder
import slick.jdbc.JdbcProfile

trait BasePlayTest
  extends FlatSpecLike
  with OptionValues
  with Matchers
  with BeforeAndAfterEach
  with BaseTest[Long]
  with BeforeAndAfterAll {

  private val testDb: Map[String, Any] = Map(
    "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:play",
    "slick.dbs.default.db.user" -> "sa",
    "slick.dbs.default.db.password" -> "")

  implicit val app = {
    val fake = new GuiceApplicationBuilder()
      .configure(testDb)
      .in(Environment.simple())
      .build
    Play.start(fake)
    fake
  }

  override lazy val unicorn: Unicorn[Long] with HasJdbcProfile =
    new LongUnicornPlay(DatabaseConfigProvider.get[JdbcProfile](app))

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