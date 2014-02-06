package org.virtuslab.unicorn.ids.services

import org.virtuslab.unicorn.ids._
import play.api.db.slick.Config.driver.simple._
import scala.Some
import scala.slick.session.Session

class UsersServiceTest extends AppTest {

  case class UserId(id: Long) extends BaseId

  object UserId extends IdCompanion[UserId]

  case class User(id: Option[UserId],
                  email: String,
                  firstName: String,
                  lastName: String) extends WithId[UserId]

  object Users extends IdTable[UserId, User]("USERS") {

    def email = column[String]("EMAIL", O.NotNull)

    def firstName = column[String]("FIRST_NAME", O.NotNull)

    def lastName = column[String]("LAST_NAME", O.NotNull)

    def base = email ~ firstName ~ lastName

    override def * = id.? ~: base <>(User.apply _, User.unapply _)

    override def insertOne(elem: User)(implicit session: Session): UserId =
      saveBase(base, User.unapply _)(elem)
  }

  trait UsersQueries extends BaseIdQueries[UserId, User] {
    override def table = Users
  }

  trait UsersService extends BaseIdService[UserId, User] with UsersQueries

  "Users Service" should "save and query users" in rollback {
    implicit session =>
    // setup
      object UsersService extends UsersService
      Users.ddl.create

      val user = User(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersService save user
      val userOpt = UsersService findById userId

      userOpt.map(_.email) shouldEqual Some(user.email)
      userOpt.map(_.firstName) shouldEqual Some(user.firstName)
      userOpt.map(_.lastName) shouldEqual Some(user.lastName)
      userOpt.flatMap(_.id) shouldNot be(None)
  }

  it should "save and query multiple users" in rollback {
    implicit session =>
    // setup
      object UsersService extends UsersService
      Users.ddl.create

      val users = (Stream from 1 take 10) map (n => User(None, "test@email.com", "Krzysztof" + n, "Nowak"))
      UsersService saveAll users
      val newUsers = UsersService.findAll()
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
  }

  it should "query existing user" in rollback {
    implicit session =>
    // setup
      object UsersService extends UsersService
      Users.ddl.create

      val user = User(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersService save user
      val user2 = UsersService findExistingById userId

      user2.email shouldEqual user.email
      user2.firstName shouldEqual user.firstName
      user2.lastName shouldEqual user.lastName
      user2.id shouldNot be(None)
  }
}
