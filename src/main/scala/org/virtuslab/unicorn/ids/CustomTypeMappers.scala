package org.virtuslab.unicorn.ids

import java.sql.{ Date, Timestamp }
import org.joda.time.{ Duration, LocalDate, DateTime }
import play.api.db.slick.Config.driver.simple._

/**
 * Custom Type mappers for Slick.
 *
 * @author Jerzy Müller, Krzysztof Romanowski
 */
trait CustomTypeMappers {

  /** Type mapper for [[org.joda.time.DateTime]] */
  implicit val dateTimeMapper = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  /** Type mapper for [[org.joda.time.LocalDate]] */
  implicit val localDateMapper = MappedColumnType.base[LocalDate, Date](
    dt => new Date(dt.toDate.getTime),
    d => new LocalDate(d.getTime)
  )

  /** Type mapper for [[org.joda.time.Duration]] */
  implicit val durationTypeMapper = MappedColumnType.base[Duration, Long](
    d => d.getMillis,
    l => Duration.millis(l)
  )
}

/** Object for [[org.virtuslab.unicorn.ids.CustomTypeMappers]] if you prefer import rather than extend. */
object CustomTypeMappers extends CustomTypeMappers