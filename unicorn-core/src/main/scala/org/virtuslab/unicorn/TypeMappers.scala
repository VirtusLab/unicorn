package org.virtuslab.unicorn

import java.sql.{ Timestamp, Date }
import org.joda.time.{ Duration, LocalDate, DateTime }

trait TypeMappers {
  self: HasJdbcDriver =>

  import driver.simple._

  /**
   * Custom Type mappers for Slick.
   */
  trait CustomTypeMappers {

    /** Type mapper for [[org.joda.time.DateTime]] */
    implicit val dateTimeMapper: BaseColumnType[DateTime] = MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime)
    )

    /** Type mapper for [[org.joda.time.LocalDate]] */
    implicit val localDateMapper: BaseColumnType[LocalDate] = MappedColumnType.base[LocalDate, Date](
      dt => new Date(dt.toDate.getTime),
      d => new LocalDate(d.getTime)
    )

    /** Type mapper for [[org.joda.time.Duration]] */
    implicit val durationTypeMapper: BaseColumnType[Duration] = MappedColumnType.base[Duration, Long](
      d => d.getMillis,
      l => Duration.millis(l)
    )
  }

  /** Object for [[org.virtuslab.unicorn.TypeMappers.CustomTypeMappers]] if you prefer import rather than extend. */
  object CustomTypeMappers extends CustomTypeMappers

}
