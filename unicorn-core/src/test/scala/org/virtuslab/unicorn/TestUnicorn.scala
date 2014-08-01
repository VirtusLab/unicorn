package org.virtuslab.unicorn

import scala.slick.driver.H2Driver

object TestUnicorn
    extends LongUnicornCore
    with HasJdbcDriver {

  override val driver = H2Driver
}
