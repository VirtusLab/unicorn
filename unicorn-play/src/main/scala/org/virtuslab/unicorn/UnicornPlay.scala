package org.virtuslab.unicorn

import play.api.data.format.Formatter
import play.api.db.slick.Config
import play.api.data.format.Formats._
import play.api.mvc.{ PathBindable, QueryStringBindable }

class UnicornPlay[Underlying](implicit val underlyingFormat: Formatter[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying])
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  override lazy val driver = Config.driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]
}

object LongUnicornPlay extends UnicornPlay[Long]
