package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn._
import org.scalatest.{ FlatSpecLike, Matchers }
import scala.Some
import org.virtuslab.unicorn.{ RollbackHelper, BaseTest }

trait AbstractUserTable {

  val unicorn: Unicorn with HasJdbcDriver

  import unicorn._
  import unicorn.driver.simple._

  case class UserId(id: Long) extends BaseId

  case class UserRow(id: Option[UserId],
    email: String,
    firstName: String,
    lastName: String) extends WithId[UserId]

  class Users(tag: Tag) extends IdTable[UserId, UserRow](tag, "USERS") {

    def email = column[String]("EMAIL", O.NotNull)

    def firstName = column[String]("FIRST_NAME", O.NotNull)

    def lastName = column[String]("LAST_NAME", O.NotNull)

    override def * = (id.?, email, firstName, lastName) <> (UserRow.tupled, UserRow.unapply)
  }

  val usersQuery: TableQuery[Users] = TableQuery[Users]

  object UsersRepository extends BaseIdRepository[UserId, UserRow, Users](usersQuery)
}

trait UsersRepositoryTest {

  self: FlatSpecLike with Matchers with RollbackHelper with AbstractUserTable =>

  import unicorn.driver.simple._

  "Users Service" should "save and query users" in rollback {
    implicit session =>
      // setup
      usersQuery.ddl.create

      val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      val userOpt = UsersRepository findById userId

      userOpt.map(_.email) shouldEqual Some(user.email)
      userOpt.map(_.firstName) shouldEqual Some(user.firstName)
      userOpt.map(_.lastName) shouldEqual Some(user.lastName)
      userOpt.flatMap(_.id) shouldNot be(None)
  }

  it should "save and query multiple users" in rollback {
    implicit session =>
      // setup
      usersQuery.ddl.create

      val users = (Stream from 1 take 10) map (n => UserRow(None, "test@email.com", "Krzysztof" + n, "Nowak"))
      UsersRepository saveAll users
      val newUsers = UsersRepository.findAll()
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
  }

  it should "query existing user" in rollback {
    implicit session =>
      // setup
      usersQuery.ddl.create

      val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      val user2 = UsersRepository findExistingById userId

      user2.email shouldEqual user.email
      user2.firstName shouldEqual user.firstName
      user2.lastName shouldEqual user.lastName
      user2.id shouldNot be(None)
  }
}

class CoreUserRepositoryTest extends BaseTest with UsersRepositoryTest with AbstractUserTable