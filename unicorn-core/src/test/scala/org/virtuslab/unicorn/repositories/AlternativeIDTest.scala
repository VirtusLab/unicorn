package org.virtuslab.unicorn.repositories

import java.util.UUID

import org.scalatest.{ FlatSpecLike, Matchers }
import org.virtuslab.unicorn._
import slick.driver.{ H2Driver, JdbcDriver }

object UUIDUnicorn extends UnicornCore[UUID] with HasJdbcDriver {
  override val driver: JdbcDriver = H2Driver
}

trait UUIDTestUnicorn {
  val unicorn: Unicorn[UUID] with HasJdbcDriver = UUIDUnicorn
}

/**
 * This class is simplified clone of Users from UsersRepositoryTest.
 * It uses UUID ids instead of Long
 */
trait UUIDTable extends UUIDTestUnicorn {

  import unicorn._
  import driver.profile._
  import driver.api._

  case class UniqueUserId(id: UUID) extends BaseId

  case class PersonRow(id: Option[UniqueUserId], name: String) extends WithId[UniqueUserId]

  class UniquePersons(tag: Tag) extends IdTable[UniqueUserId, PersonRow](tag, "U_USERS") {
    def name = column[String]("NAME")

    override def * = (id.?, name) <> (PersonRow.tupled, PersonRow.unapply)
  }

  //provides custom ddl query to generate UUID primary keys
  object UniquePersons {
    val customDDL = DDL(
      """CREATE TABLE IF NOT EXISTS "U_USERS" ("id" UUID default RANDOM_UUID() PRIMARY KEY , "NAME" VARCHAR(255) NOT NULL);""",
      "DROP TABLE U_USERS;")
    val ddl = createDDLInvoker(customDDL)
  }

  val personsQuery = TableQuery[UniquePersons]

  object PersonsRepository extends BaseIdRepository[UniqueUserId, PersonRow, UniquePersons](personsQuery)

}

trait PersonUUIDTest extends FlatSpecLike {
  self: FlatSpecLike with Matchers with RollbackHelper[UUID] with UUIDTable =>

  "Persons Repository" should "work fine with UUID id" in rollback { implicit session =>
    UniquePersons.ddl.create

    val person = PersonRow(None, "Alexander")
    val personId = PersonsRepository save person

    val foundPerson = PersonsRepository findById personId

    foundPerson.flatMap(_.id) shouldNot be(None)
    foundPerson.map(_.name) shouldEqual Some(person.name)
  }
}

class AlternativeIDTest extends BaseTest[UUID] with UUIDTable with PersonUUIDTest
