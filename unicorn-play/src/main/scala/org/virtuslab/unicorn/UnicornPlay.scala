package org.virtuslab.unicorn

import play.api.Play
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Format
import play.api.mvc.{ PathBindable, QueryStringBindable }
import slick.driver.JdbcProfile

trait UnicornPlayLike[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  def underlyingFormatter: Formatter[Underlying]

  def underlyingFormat: Format[Underlying]

  def underlyingQueryStringBinder: QueryStringBindable[Underlying]

  def underlyingPathBinder: PathBindable[Underlying]

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]

}

class UnicornPlay[Underlying](override val driver: JdbcProfile)(implicit val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying])
    extends UnicornPlayLike[Underlying]

object CurrentPlay {

  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  lazy val driver = dbConfig.driver

}

object LongUnicornPlay extends UnicornPlay[Long](CurrentPlay.driver)
