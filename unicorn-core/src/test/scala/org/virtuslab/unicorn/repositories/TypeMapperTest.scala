package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.TestUnicorn.driver.simple._
import org.virtuslab.unicorn.{ BaseTest, LongTestUnicorn }
import org.joda.time.{ DateTime, Duration, LocalDate }

class TypeMapperTest extends BaseTest[Long] with LongTestUnicorn {

  import unicorn._

  behavior of classOf[CustomTypeMappers].getSimpleName

  case class JodaRow(dateTime: DateTime,
    duration: Duration,
    localDate: LocalDate)

  class Joda(tag: Tag) extends BaseTable[JodaRow](tag, "JODA") {

    def dateTime = column[DateTime]("EMAIL", O.NotNull)

    def duration = column[Duration]("FIRST_NAME", O.NotNull)

    def localDate = column[LocalDate]("LAST_NAME", O.NotNull)

    override def * = (dateTime, duration, localDate) <> (JodaRow.tupled, JodaRow.unapply)
  }

  val jodaQuery: TableQuery[Joda] = TableQuery[Joda]

  it should "provide mappings for joda.time types" in rollback {
    implicit session =>
      jodaQuery.ddl.create

      val joda = JodaRow(DateTime.now(), Duration.millis(120), LocalDate.now())
      jodaQuery insert joda

      jodaQuery.first shouldEqual joda
  }
}
