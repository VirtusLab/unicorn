package org.virtuslab.unicorn.repositories

import org.scalatest.{ FlatSpecLike, Matchers, OptionValues }
import org.virtuslab.unicorn.{ BaseTest, _ }

import scala.concurrent.ExecutionContext.Implicits.global

trait AbstractUserTable {

  val unicorn: Unicorn[Long] with HasJdbcProfile
  val identifiers: Identifiers[Long]

  import unicorn._
  import unicorn.profile.api._
  import identifiers._

  case class UserId(id: Long) extends BaseId[Long]

  object UserId extends CoreCompanion[UserId]

  case class UserRow(
    id: Option[UserId],
    email: String,
    firstName: String,
    lastName: String
  ) extends WithId[Long, UserId]

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
  self: FlatSpecLike with Matchers with BaseTest[Long] with AbstractUserTable =>

  "Users Service" should "save and query users" in runWithRollback {
    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- UsersRepository.create
      userId <- UsersRepository.save(user)
      user <- UsersRepository.findById(userId)
    } yield user

    actions map { userOpt =>
      userOpt shouldBe defined

      userOpt.value should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      userOpt.value.id shouldBe defined
    }
  }

  it should "save and query multiple users" in runWithRollback {
    val users = (Stream from 1 take 10) map (n => UserRow(None, "test@email.com", "Krzysztof" + n, "Nowak"))

    // setup
    val actions = for {
      _ <- UsersRepository.create
      _ <- UsersRepository saveAll users
      all <- UsersRepository.findAll()
    } yield all

    actions map { newUsers =>
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
    }
  }

  it should "query existing user" in runWithRollback {
    val blankUser = UserRow(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- UsersRepository.create
      userId <- UsersRepository save blankUser
      user <- UsersRepository.findExistingById(userId)
    } yield user

    actions map { user2 =>
      user2 should have(
        'email(blankUser.email),
        'firstName(blankUser.firstName),
        'lastName(blankUser.lastName)
      )
      user2.id shouldBe defined
    }
  }

  it should "update existing user" in runWithRollback {
    val blankUser = UserRow(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- UsersRepository.create
      userId <- UsersRepository save blankUser
      user <- UsersRepository.findExistingById(userId)
      _ <- UsersRepository save user.copy(firstName = "Jerzy", lastName = "Muller")
      updatedUser <- UsersRepository.findExistingById(userId)
    } yield (userId, updatedUser)

    actions map {
      case (userId, updatedUser) =>
        updatedUser should have(
          'email("test@email.com"),
          'firstName("Jerzy"),
          'lastName("Muller"),
          'id(Some(userId))
        )
    }
  }

  it should "query all ids" in runWithRollback {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- UsersRepository.create
      ids <- UsersRepository saveAll users
      allIds <- UsersRepository.allIds()
    } yield (ids, allIds)

    actions map {
      case (ids, allIds) =>
        allIds shouldEqual ids
    }
  }

  it should "sort users by id" in runWithRollback {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- UsersRepository.create
      ids <- UsersRepository saveAll users
      users <- UsersRepository.findAll()
    } yield (ids, users)

    actions map {
      case (ids, users) =>
        val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
        users.sortBy(_.id) shouldEqual usersWithIds
    }
  }

  it should "query multiple users by ids" in runWithRollback {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- UsersRepository.create
      ids <- UsersRepository saveAll users
      allUsers <- UsersRepository.findAll
      selectedUsers: Seq[UserRow] = {
        val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
        Seq(usersWithIds.head, usersWithIds.last)
      }
      foundSelectedUsers <- UsersRepository.findByIds(selectedUsers.flatMap(_.id))
    } yield (allUsers, foundSelectedUsers, selectedUsers)

    actions map {
      case (allUsers, foundSelectedUsers, selectedUsers) =>
        allUsers.size shouldEqual 3
        foundSelectedUsers shouldEqual selectedUsers
    }
  }

  it should "copy user by id" in runWithRollback {

    val user = UserRow(None, "test1@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- UsersRepository.create
      id <- UsersRepository.save(user)
      idOfCopy <- UsersRepository.copyAndSave(id)
      copiedUser <- UsersRepository.findById(idOfCopy)
    } yield (copiedUser.value)

    actions map { copiedUser =>
      copiedUser.id shouldNot be(user.id)

      copiedUser should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
    }
  }

  it should "delete user by id" in runWithRollback {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- UsersRepository.create
      ids <- UsersRepository saveAll users
      initialUsers <- UsersRepository.findAll
      _ <- UsersRepository.deleteById(ids(1))
      resultingUsers <- UsersRepository.findAll
    } yield (ids, initialUsers, resultingUsers)

    actions map {
      case (ids, initialUsers, resultingUsers) =>
        initialUsers should have size users.size
        val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
        val remainingUsers = Seq(usersWithIds.head, usersWithIds.last)
        resultingUsers shouldEqual remainingUsers
    }

  }

  it should "delete all users" in runWithRollback {
    val users = Seq(
      UserRow(None, "test1@email.com", "Krzysztof", "Nowak"),
      UserRow(None, "test2@email.com", "Janek", "Nowak"),
      UserRow(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- UsersRepository.create
      ids <- UsersRepository saveAll users
      initialUsers <- UsersRepository.findAll
      _ <- UsersRepository.deleteAll()
      resultingUsers <- UsersRepository.findAll
    } yield (initialUsers, resultingUsers)

    actions map {
      case (initialUsers, resultingUsers) =>
        initialUsers should have size users.size
        resultingUsers shouldBe empty
    }
  }

  it should "create and drop table" in runWithRollback {
    val actions = for {
      _ <- UsersRepository.create
      _ <- UsersRepository.drop
    } yield ()

    actions
  }
}

class CoreUserRepositoryTest extends BaseTest[Long] with UsersRepositoryTest with AbstractUserTable {
  override lazy val unicorn = TestUnicorn
  override val identifiers: Identifiers[Long] = LongUnicornIdentifiers
}