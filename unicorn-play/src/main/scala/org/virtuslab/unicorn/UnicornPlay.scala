package org.virtuslab.unicorn

import com.google.inject.Inject
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.Database
import play.api.libs.json.Format
import play.api.mvc.{ PathBindable, QueryStringBindable }
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

abstract class UnicornPlayLike[Underlying](dbConfig: DatabaseConfig[JdbcProfile])
    extends Unicorn[Underlying]
    with HasJdbcDriver {

  override lazy val driver = dbConfig.driver

  val db = dbConfig.db

}

abstract class UnicornPlay[Underlying](dbConfig: DatabaseConfig[JdbcProfile])
  extends UnicornPlayLike[Underlying](dbConfig)

class LongUnicornPlay @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) extends UnicornPlay[Long](dbConfig)

object LongUnicornPlayIdentifiers extends PlayIdentifiersImpl[Long] {
  override val ordering: Ordering[Long] = implicitly[Ordering[Long]]
  override type IdCompanion[Id <: BaseId[Long]] = PlayCompanion[Id]
}

abstract class PlayIdentifiersImpl[Underlying](implicit val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying]) extends PlayIdentifiers[Underlying] with Identifiers[Underlying]