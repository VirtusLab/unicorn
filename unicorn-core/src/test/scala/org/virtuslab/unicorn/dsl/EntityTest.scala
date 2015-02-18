package org.virtuslab.unicorn.dsl

import org.virtuslab.unicorn.{BaseTest, TestUnicorn, HasJdbcDriver, Unicorn}

import scala.slick.lifted

/**
 * Author: Krzysztof Romanowski
 */
class EntityTest extends BaseTest[Long] {

  val unicorn: Unicorn[Long] with HasJdbcDriver = TestUnicorn

  import unicorn.driver.simple._

  object User extends Entity(unicorn) {

    case class Row(id: Option[Id], name: String) extends BaseRow

    class Table(tag: Tag) extends BaseTable(tag, "USERS") {
      def name = column[String]("name")

      def * = (id.?, name) <>(Row.tupled, Row.unapply)
    }

    override def query: TableQuery[Table] = TableQuery[Table]
  }

  //somewhere in other file...
  object UserRepository extends User.BaseRepository

  object File extends Entity(unicorn) {

    case class Row(id: Option[Id], name: String, user: User.Id) extends BaseRow

    class Table(tag: Tag) extends BaseTable(tag, "USERS") {
      def name = column[String]("name")

      def userId = column[User.Id]("userId")

      def * = (id.?, name, userId) <>(Row.tupled, Row.unapply)
    }

    override def query: TableQuery[Table] = TableQuery[Table]
  }

  //somewhere in other file...
  object FileRepository extends User.BaseRepository

  "dsl" should "work" in rollback { implicit session =>
    UserRepository.findAll() should be('empty)
  }

}
