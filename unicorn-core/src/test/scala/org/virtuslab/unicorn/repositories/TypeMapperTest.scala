package org.virtuslab.unicorn.repositories

import org.joda.time.{ DateTime, Duration, LocalDate }
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import org.virtuslab.unicorn.TestUnicorn.profile.api._
import org.virtuslab.unicorn.{ BaseTest, LongTestUnicorn }

import scala.concurrent.ExecutionContext.Implicits.global

class TypeMapperTest extends BaseTest[Long] with LongTestUnicorn {

  import unicorn._

  behavior of classOf[CustomTypeMappers].getSimpleName

  case class JodaRow(
    dateTime: DateTime,
    duration: Duration,
    localDate: LocalDate
  )

  class Joda(tag: Tag) extends BaseTable[JodaRow](tag, "JODA") {

    def dateTime = column[DateTime]("EMAIL")

    def duration = column[Duration]("FIRST_NAME")

    def localDate = column[LocalDate]("LAST_NAME")

    override def * = (dateTime, duration, localDate) <> (JodaRow.tupled, JodaRow.unapply)
  }

  val jodaQuery: TableQuery[Joda] = TableQuery[Joda]

  it should "provide mappings for joda.time types" in runWithRollback {
    val joda = JodaRow(DateTime.now(), Duration.millis(120), LocalDate.now())
    val actions = for {
      _ <- jodaQuery.schema.create
      _ <- jodaQuery += (joda)
      first <- jodaQuery.result.headOption
    } yield first

    actions map { first =>
      first shouldEqual Some(joda)
    }
  }
}
