package org.virtuslab.unicorn

import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Format
import play.api.mvc.{ PathBindable, QueryStringBindable }
import slick.driver.JdbcDriver
import slick.lifted.Rep

import scala.language.implicitConversions

trait UnicornPlayLike[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  def underlyingFormatter: Formatter[Underlying]

  def underlyingFormat: Format[Underlying]

  def underlyingQueryStringBinder: QueryStringBindable[Underlying]

  def underlyingPathBinder: PathBindable[Underlying]

  override lazy val driver: JdbcDriver = DatabaseConfigProvider.get(play.api.Play.current).driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]

  @deprecated("Use methods with DBIO.", "0.7.2")
  implicit def syncRep[T](rep: Rep[T]): SyncRep[T] = new SyncRep(rep, driver)
}

class UnicornPlay[Underlying](implicit
  val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying])
    extends UnicornPlayLike[Underlying]

object LongUnicornPlay extends UnicornPlay[Long]
