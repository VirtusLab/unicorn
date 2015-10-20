package org.virtuslab.unicorn

import slick.driver.H2Driver

object TestUnicorn
    extends LongUnicornCore
    with HasJdbcDriver {

  override lazy val driver = H2Driver

}
