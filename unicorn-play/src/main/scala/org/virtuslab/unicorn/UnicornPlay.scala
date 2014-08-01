package org.virtuslab.unicorn

import play.api.data.format.Formatter
import play.api.db.slick.Config

protected[unicorn] trait UnicornPlay[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  override lazy val driver = Config.driver

  override type IdCompanion[Id <: MappedId] = PlayCompanion[Id]
}

trait LongFormatter {
  implicit val underlyingFormat: Formatter[Long] = play.api.data.format.Formats.longFormat
}

trait LongUnicornPlay extends LongFormatter with UnicornPlay[Long] {

  type BaseId = MappedId // For backward capability
}

object LongUnicornPlay extends LongUnicornPlay

