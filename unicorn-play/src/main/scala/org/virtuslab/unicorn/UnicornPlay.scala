package org.virtuslab.unicorn

import play.api.db.slick.Config

protected[unicorn] trait UnicornPlay
    extends Unicorn
    with PlayIdentifiers
    with HasJdbcDriver {

  override lazy val driver = Config.driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]
}

object UnicornPlay extends UnicornPlay
