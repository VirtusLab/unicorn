package org.virtuslab.unicorn

import slick.driver.JdbcProfile

import play.api.libs.json.Format
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ PathBindable, QueryStringBindable }

trait UnicornPlayLike[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  private val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def underlyingFormatter: Formatter[Underlying]

  def underlyingFormat: Format[Underlying]

  def underlyingQueryStringBinder: QueryStringBindable[Underlying]

  def underlyingPathBinder: PathBindable[Underlying]

  override lazy val driver = Config.driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]

  override val db = Config.driver.backend.Database
}

class UnicornPlay[Underlying](implicit val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying])
    extends UnicornPlayLike[Underlying]

object LongUnicornPlay extends UnicornPlay[Long]
