package org.virtuslab.unicorn

import play.api.data.format.Formatter
import play.api.db.slick.Config

protected[unicorn] class UnicornPlay[Underlying](implicit val formatter: Formatter[Underlying])
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  override lazy val driver = Config.driver

  trait Dupa[Id] extends PlayCompanion[Id] {
    override val underlyingFormat: Formatter[Long] = play.api.data.format.Formats.longFormat
  }

  override type IdCompanion[Id <: MappedId] = Dupa[Id]
}

trait LongUnicornPlay extends UnicornPlay[Long] {
  type BaseId = MappedId // For backward capability
}

object LongUnicornPlay extends LongUnicornPlay
