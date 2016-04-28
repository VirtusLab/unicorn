package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn._
import org.scalatest.{ OptionValues, FlatSpecLike, Matchers }
import org.virtuslab.unicorn.{ RollbackHelper, BaseTest }
import scala.concurrent.ExecutionContext.Implicits.global

trait AbstractUserTable {

  val unicorn: Unicorn[Long] with HasJdbcDriver

  import unicorn._
  import unicorn.driver.api._

  case class UserId(id: Long) extends BaseId

  object UserId extends CoreCompanion[UserId]

  case class UserRow(
    id: Option[UserId],
    email: String,
    firstName: String,
    lastName: String
  ) extends WithId[UserId]

  class Users(tag: Tag) extends IdTable[UserId, UserRow](tag, "USERS") {

    def email = column[String]("EMAIL")

    def firstName = column[String]("FIRST_NAME")

    def lastName = column[String]("LAST_NAME")

    override def * = (id.?, email, firstName, lastName) <> (UserRow.tupled, UserRow.unapply)
  }

  val usersQuery: TableQuery[Users] = TableQuery[Users]

  object UsersRepository extends BaseIdRepository[UserId, UserRow, Users](usersQuery)

}

trait UsersRepositoryTest extends OptionValues {
  self: FlatSpecLike with Matchers with RollbackHelper[Long] with AbstractUserTable =>

  "Users Service" should "save and query users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      val userOpt = UsersRepository findById userId

      userOpt shouldBe defined

      userOpt.value should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      userOpt.value.id shouldBe defined
  }

  "Users Service" should "save and query users using DBIO" in rollbackAction {

    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
    for {
      // GIVEN
      _ <- UsersRepository.createAction()
      // WHEN
      userId <- UsersRepository.saveAction(user)
      userOpt <- UsersRepository.findByIdAction(userId)
    } yield {
      // THEN
      userOpt shouldBe defined
      userOpt.value should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      userOpt.value.id shouldBe defined
    }
  }

  it should "save and query multiple users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = (Stream from 1 take 10) map (n => UserRow(None, "test@email.com", "Krzysztof" + n, "Nowak"))
      UsersRepository saveAll users
      val newUsers = UsersRepository.findAll()
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
  }

  it should "save and query multiple users using DBIO" in rollbackAction {
    val users = (Stream from 1 take 10) map (n => UserRow(None, "test@email.com", "Krzysztof" + n, "Nowak"))
    for {
      _ <- UsersRepository.createAction()
      _ <- UsersRepository.saveAllAction(users)
      newUsers <- UsersRepository.findAllAction()
    } yield {
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
    }
  }

  it should "query existing user" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      val user2 = UsersRepository findExistingById userId

      user2 should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      user2.id shouldBe defined
  }

  it should "query existing user using DBIO" in rollbackAction {
    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
    for {
      _ <- UsersRepository.createAction()
      userId <- UsersRepository.saveAction(user)
      user2 <- UsersRepository.findExistingByIdAction(userId)
    } yield {
      user2 should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      user2.id shouldBe defined
    }
  }

  it should "update existing user" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      var user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      user = UsersRepository findExistingById userId

      UsersRepository save user.copy(firstName = "Jerzy", lastName = "Muller")

      user = UsersRepository findExistingById userId

      user should have(
        'email("test@email.com"),
        'firstName("Jerzy"),
        'lastName("Muller"),
        'id(Some(userId))
      )
  }

  it should "update existing user using DBIO" in rollbackAction {
    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
    for {
      _ <- UsersRepository.createAction()
      userId <- UsersRepository.saveAction(user)
      foundUser <- UsersRepository.findExistingByIdAction(userId)
      _ <- UsersRepository.saveAction(foundUser.copy(firstName = "Jerzy", lastName = "Muller"))
      resultUser <- UsersRepository.findExistingByIdAction(userId)
    } yield {
      resultUser should have(
        'email("test@email.com"),
        'firstName("Jerzy"),
        'lastName("Muller"),
        'id(Some(userId))
      )
    }
  }

  it should "query all ids" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
        UserRow(None, "test2@email.com", "Janek", "Nowak"),
        UserRow(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users

      UsersRepository.allIds() shouldEqual ids
  }

  it should "query all ids using DBIO" in rollbackAction {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )
    for {
      _ <- UsersRepository.createAction()
      ids <- UsersRepository.saveAllAction(users)
      allIds <- UsersRepository.allIdsAction()
    } yield {
      allIds shouldEqual ids
    }
  }

  it should "sort users by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
        UserRow(None, "test2@email.com", "Janek", "Nowak"),
        UserRow(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }

      UsersRepository.findAll().sortBy(_.id) shouldEqual usersWithIds
  }

  it should "sort users by id using DBIO" in rollbackAction {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )
    for {
      _ <- UsersRepository.createAction()
      ids <- UsersRepository.saveAllAction(users)
      foundUsers <- UsersRepository.findAllAction().map(_.sortBy(_.id))
    } yield {
      val usersWithIds = {
        (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      }
      foundUsers shouldEqual usersWithIds
    }
  }

  it should "query multiple users by ids" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
        UserRow(None, "test2@email.com", "Janek", "Nowak"),
        UserRow(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      UsersRepository.findAll().size shouldEqual 3

      val selectedUsers = Seq(usersWithIds.head, usersWithIds.last)

      UsersRepository.findByIds(selectedUsers.flatMap(_.id)) shouldEqual selectedUsers
  }

  it should "query multiple users by ids using DBIO" in rollbackAction {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )
    def selectedUsers(ids: Seq[UserId]): Seq[UserRow] = {
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      Seq(usersWithIds.head, usersWithIds.last)
    }

    for {
      _ <- UsersRepository.createAction()
      ids <- UsersRepository.saveAllAction(users)
      foundUsers <- UsersRepository.findAllAction()
      foundSelectedUsers <- UsersRepository.findByIdsAction(selectedUsers(ids).flatMap(_.id))
    } yield {
      foundUsers.size shouldEqual 3
      foundSelectedUsers shouldEqual selectedUsers(ids)
    }
  }

  it should "copy user by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = UserRow(None, "test1@email.com", "Krzysztof", "Nowak")

      val id = UsersRepository save user

      val idOfCopy = UsersRepository.copyAndSave(id)
      val copiedUser = idOfCopy.flatMap(UsersRepository.findById).value

      copiedUser.id shouldNot be(user.id)

      copiedUser should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
  }

  it should "copy user by id using DBIO" in rollbackAction {
    val user = UserRow(None, "test1@email.com", "Krzysztof", "Nowak")
    for {
      _ <- UsersRepository.createAction()
      id <- UsersRepository.saveAction(user)
      idOfCopy <- UsersRepository.copyAndSaveAction(id)
      copiedUser <- UsersRepository.findByIdAction(idOfCopy.get)
    } yield {
      copiedUser shouldBe defined
      copiedUser.get.id shouldNot be(user.id)
      copiedUser.get should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
    }
  }

  it should "delete user by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
        UserRow(None, "test2@email.com", "Janek", "Nowak"),
        UserRow(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      UsersRepository.findAll() should have size users.size

      UsersRepository.deleteById(ids(1))
      val remainingUsers = Seq(usersWithIds.head, usersWithIds.last)

      UsersRepository.findAll() shouldEqual remainingUsers
  }

  it should "delete user by id using DBIO" in rollbackAction {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )
    val intermediateAction = for {
      _ <- UsersRepository.createAction()
      ids <- UsersRepository.saveAllAction(users)
      allUsers <- UsersRepository.findAllAction()
    } yield {
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      allUsers should have size users.size
      usersWithIds
    }

    for {
      usersWithIds <- intermediateAction
      _ <- UsersRepository.deleteByIdAction(usersWithIds(1).id.get)
      allLeft <- UsersRepository.findAllAction()
    } yield {
      val remainingUsers = Seq(usersWithIds.head, usersWithIds.last)
      allLeft shouldEqual remainingUsers
    }
  }

  it should "delete all users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
        UserRow(None, "test2@email.com", "Janek", "Nowak"),
        UserRow(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      UsersRepository.findAll() should have size users.size

      UsersRepository.deleteAll()

      UsersRepository.findAll() shouldBe empty
  }

  it should "delete all users using DBIO" in rollbackAction {

    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )
    val intermediateAction = for {
      _ <- UsersRepository.createAction()
      ids <- UsersRepository.saveAllAction(users)
      all <- UsersRepository.findAllAction()
    } yield {
      all should have size users.size
    }

    for {
      _ <- intermediateAction
      _ <- UsersRepository.deleteAllAction()
      all <- UsersRepository.findAllAction()
    } yield {
      all shouldBe empty
    }
  }

  it should "create and drop table" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()
      UsersRepository.drop()
  }

  it should "create and drop table using DBIO" in rollbackAction {
    for {
      _ <- UsersRepository.createAction()
      _ <- UsersRepository.dropAction()
    } yield ()
  }
}

class CoreUserRepositoryTest extends BaseTest[Long] with UsersRepositoryTest with AbstractUserTable {
  override lazy val unicorn = TestUnicorn
}