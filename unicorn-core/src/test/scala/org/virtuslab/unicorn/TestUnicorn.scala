package org.virtuslab.unicorn

import slick.jdbc.H2Profile

object LongUnicornIdentifiers extends Identifiers[Long] {
  override def ordering: Ordering[Long] = implicitly[Ordering[Long]]

  override type IdCompanion[Id <: BaseId[Long]] = CoreCompanion[Id]
}

object TestUnicorn
  extends LongUnicornCore
  with HasJdbcProfile {

  override lazy val profile = H2Profile
}
