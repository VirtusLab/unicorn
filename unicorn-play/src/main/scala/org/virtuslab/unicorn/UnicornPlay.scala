package org.virtuslab.unicorn

import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.Config
import play.api.mvc.{ PathBindable, QueryStringBindable }

trait UnicornPlayLike[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  def underlyingFormat: Formatter[Underlying]

  def underlyingQueryStringBinder: QueryStringBindable[Underlying]

  def underlyingPathBinder: PathBindable[Underlying]

  override lazy val driver = Config.driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]
}

class UnicornPlay[Underlying](implicit val underlyingFormat: Formatter[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying])
    extends UnicornPlayLike[Underlying]

object LongUnicornPlay extends UnicornPlay[Long]
