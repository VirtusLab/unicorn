package org.virtuslab.unicorn.ids

import java.sql.{ Date, Timestamp }
import org.joda.time.{ LocalDate, DateTime }
import scala.slick.lifted.MappedTypeMapper

/**
 * Custom Type mappers for Slick.
 *
 * @author Jerzy Müller, Krzysztof Romanowski
 */
private[ids] trait CustomTypeMappers {

  /** Type mapper for [[org.joda.time.DateTime]] */
  implicit val dateTimeMapper = MappedTypeMapper.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  /** Type mapper for [[org.joda.time.LocalDate]] */
  implicit val localDateMapper = MappedTypeMapper.base[LocalDate, Date](
    dt => new Date(dt.toDate.getTime),
    d => new LocalDate(d.getTime)
  )
}

/** Object for [[play.api.db.slick.ids.CustomTypeMappers]] if you prefer import rather than extend. */
object CustomTypeMappers extends CustomTypeMappers