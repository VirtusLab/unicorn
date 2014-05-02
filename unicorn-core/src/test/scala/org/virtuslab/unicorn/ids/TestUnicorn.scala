package org.virtuslab.unicorn.ids

import scala.slick.driver.H2Driver

object TestUnicorn
    extends UnicornCore
    with HasJdbcDriver {
  override val driver = H2Driver
}
