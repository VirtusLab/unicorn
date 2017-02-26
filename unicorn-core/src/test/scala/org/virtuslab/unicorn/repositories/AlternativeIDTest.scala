package org.virtuslab.unicorn.repositories

import java.util.UUID

import org.scalatest.{ FlatSpecLike, Matchers }
import org.virtuslab.unicorn.TestUnicorn.profile.api._
import org.virtuslab.unicorn._
import slick.jdbc.H2Profile

import scala.concurrent.ExecutionContext.Implicits.global

object UUIDUnicorn extends UnicornCore[UUID] with HasJdbcProfile {
  override val profile = H2Profile
}

object UUIDUnicornIdentifiers extends Identifiers[UUID] {
  override def ordering: Ordering[UUID] = implicitly[Ordering[UUID]]
}

trait UUIDTestUnicorn {
  val unicorn: Unicorn[UUID] with HasJdbcProfile = UUIDUnicorn
}

/**
 * This class is simplified clone of Users from UsersRepositoryTest.
 * It uses UUID ids instead of Long
 */
trait UUIDTable extends UUIDTestUnicorn {

  import unicorn._

  case class UniqueUserId(id: UUID) extends BaseId[UUID]

  case class PersonRow(id: Option[UniqueUserId], name: String) extends WithId[UUID, UniqueUserId]

  class UniquePersons(tag: Tag) extends IdTable[UniqueUserId, PersonRow](tag, "U_USERS") {
    def name = column[String]("NAME")

    override def * = (id.?, name) <> (PersonRow.tupled, PersonRow.unapply)
  }

  //provides custom ddl query to generate UUID primary keys
  object UniquePersons {
    val CreateSql = sqlu"""CREATE TABLE IF NOT EXISTS "U_USERS" ("id" UUID default RANDOM_UUID() PRIMARY KEY , "NAME" VARCHAR(255) NOT NULL);"""
    val CreateDdl = CreateSql.map(_ => ())
    val DropSql = sqlu"DROP TABLE U_USERS;"
  }

  val personsQuery = TableQuery[UniquePersons]

  object PersonsRepository extends BaseIdRepository[UniqueUserId, PersonRow, UniquePersons](personsQuery)

}

trait PersonUUIDTest extends FlatSpecLike {
  self: FlatSpecLike with Matchers with BaseTest[UUID] with UUIDTable =>

  "Persons Repository" should "work fine with UUID id" in runWithRollback {
    val person = PersonRow(None, "Alexander")

    val actions = for {
      _ <- UniquePersons.CreateDdl
      personId <- PersonsRepository save person
      foundPerson <- PersonsRepository findById personId
    } yield foundPerson

    actions map { foundPerson =>
      foundPerson.flatMap(_.id) shouldNot be(None)
      foundPerson.map(_.name) shouldEqual Some(person.name)
    }
  }
}

class AlternativeIDTest extends BaseTest[UUID] with UUIDTable with PersonUUIDTest
