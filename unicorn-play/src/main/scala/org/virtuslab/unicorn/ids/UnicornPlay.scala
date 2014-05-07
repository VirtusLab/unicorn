package org.virtuslab.unicorn.ids

import play.api.db.slick.Config

protected[unicorn] trait UnicornPlay
    extends Unicorn
    with PlayIdentifiers
    with HasJdbcDriver {

  override val driver = Config.driver

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]
}

object UnicornPlay extends UnicornPlay
