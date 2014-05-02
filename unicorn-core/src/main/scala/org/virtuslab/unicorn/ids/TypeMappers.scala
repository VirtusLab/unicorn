package org.virtuslab.unicorn.ids

import java.sql.Date
import java.sql.Timestamp
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate

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

  /** Object for [[org.virtuslab.unicorn.ids.TypeMappers.CustomTypeMappers]] if you prefer import rather than extend. */
  object CustomTypeMappers extends CustomTypeMappers

}
