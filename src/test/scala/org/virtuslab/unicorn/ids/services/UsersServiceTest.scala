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
}