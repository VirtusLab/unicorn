package org.virtuslab.unicorn

import javax.inject.{ Inject, Singleton }

import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Format
import play.api.mvc.{ PathBindable, QueryStringBindable }
import slick.basic.DatabaseConfig
import slick.jdbc.{ JdbcBackend, JdbcProfile }

trait UnicornWrapper[Underlying] {
  protected val unicorn: UnicornPlay[Underlying]
}

abstract class UnicornPlayLike[Underlying](dbConfig: DatabaseConfig[JdbcProfile])
    extends Unicorn[Underlying]
    with HasJdbcProfile {

  val profile: JdbcProfile = dbConfig.profile

  val db: JdbcBackend#DatabaseDef = dbConfig.db
}

abstract class UnicornPlay[Underlying](dbConfig: DatabaseConfig[JdbcProfile])
  extends UnicornPlayLike[Underlying](dbConfig)

abstract class PlayIdentifiersImpl[Underlying](implicit
  val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying]) extends PlayIdentifiers[Underlying] with Identifiers[Underlying]

@Singleton()
class LongUnicornPlay @Inject() (dbConfig: DatabaseConfig[JdbcProfile])
  extends UnicornPlay[Long](dbConfig)

@Singleton()
class LongUnicornPlayJDBC @Inject() (databaseConfigProvider: DatabaseConfigProvider)
  extends LongUnicornPlay(databaseConfigProvider.get[JdbcProfile])

object LongUnicornPlayIdentifiers extends PlayIdentifiersImpl[Long] {
  override val ordering: Ordering[Long] = implicitly[Ordering[Long]]
  override type IdCompanion[Id <: BaseId[Long]] = PlayCompanion[Id]
}