package org.virtuslab.unicorn.repositories

import org.scalatest.FlatSpecLike
import org.scalatest.Matchers
import org.scalatest.OptionValues
import org.virtuslab.unicorn.WithId
import org.virtuslab.unicorn.BaseTest
import org.virtuslab.unicorn._

import scala.concurrent.ExecutionContext.Implicits.global

trait DSLUserTable {

  val unicorn: Unicorn[Long] with HasJdbcProfile
  val identifiers: Identifiers[Long]

  import unicorn._
  import unicorn.profile.api._

  private def tableName = getClass.getSimpleName + "_DSL_USERS"

  object User extends EntityDsl(identifiers) {
    case class Row(id: Option[Id], email: String, firstName: String, lastName: String) extends WithId[Long, Id]

    class Table(tag: Tag) extends BaseTable(tag, tableName) {
      def email = column[String]("EMAIL")

      def firstName = column[String]("FIRST_NAME")

      def lastName = column[String]("LAST_NAME")

      override def * = (id.?, email, firstName, lastName).mapTo[Row]
    }

    override val Repository = new DslRepository(TableQuery[Table])
  }
}

trait UserRepositoryDSLTest extends OptionValues {
  self: FlatSpecLike with Matchers with BaseTest[Long] with DSLUserTable =>

  "Users Service" should "save and query users" in runWithRollback {
    val user = User.Row(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- User.Repository.create
      userId <- User.Repository.save(user)
      user <- User.Repository.findById(userId)
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
    val users = (Stream from 1 take 10) map (n => User.Row(None, "test@email.com", "Krzysztof" + n, "Nowak"))

    // setup
    val actions = for {
      _ <- User.Repository.create
      _ <- User.Repository saveAll users
      all <- User.Repository.findAll()
    } yield all

    actions map { newUsers =>
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
    }
  }

  it should "query existing user" in runWithRollback {
    val blankUser = User.Row(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- User.Repository.create
      userId <- User.Repository save blankUser
      user <- User.Repository.findExistingById(userId)
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
    val blankUser = User.Row(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- User.Repository.create
      userId <- User.Repository save blankUser
      user <- User.Repository.findExistingById(userId)
      _ <- User.Repository save user.copy(firstName = "Jerzy", lastName = "Muller")
      updatedUser <- User.Repository.findExistingById(userId)
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
      User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
      User.Row(None, "test2@email.com", "Janek", "Nowak"),
      User.Row(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- User.Repository.create
      ids <- User.Repository saveAll users
      allIds <- User.Repository.allIds()
    } yield (ids, allIds)

    actions map {
      case (ids, allIds) =>
        allIds shouldEqual ids
    }
  }

  it should "sort users by id" in runWithRollback {
    val users = Seq(
      User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
      User.Row(None, "test2@email.com", "Janek", "Nowak"),
      User.Row(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- User.Repository.create
      ids <- User.Repository saveAll users
      users <- User.Repository.findAll()
    } yield (ids, users)

    actions map {
      case (ids, users) =>
        val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
        users.sortBy(_.id) shouldEqual usersWithIds
    }
  }

  it should "query multiple users by ids" in runWithRollback {
    val users = Seq(
      User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
      User.Row(None, "test2@email.com", "Janek", "Nowak"),
      User.Row(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- User.Repository.create
      ids <- User.Repository saveAll users
      allUsers <- User.Repository.findAll
      selectedUsers: Seq[User.Row] = {
        val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
        Seq(usersWithIds.head, usersWithIds.last)
      }
      foundSelectedUsers <- User.Repository.findByIds(selectedUsers.flatMap(_.id))
    } yield (allUsers, foundSelectedUsers, selectedUsers)

    actions map {
      case (allUsers, foundSelectedUsers, selectedUsers) =>
        allUsers.size shouldEqual 3
        foundSelectedUsers shouldEqual selectedUsers
    }
  }

  it should "copy user by id" in runWithRollback {

    val user = User.Row(None, "test1@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- User.Repository.create
      id <- User.Repository.save(user)
      idOfCopy <- User.Repository.copyAndSave(id)
      copiedUser <- User.Repository.findById(idOfCopy)
    } yield copiedUser.value

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
      User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
      User.Row(None, "test2@email.com", "Janek", "Nowak"),
      User.Row(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- User.Repository.create
      ids <- User.Repository saveAll users
      initialUsers <- User.Repository.findAll
      _ <- User.Repository.deleteById(ids(1))
      resultingUsers <- User.Repository.findAll
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
      User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
      User.Row(None, "test2@email.com", "Janek", "Nowak"),
      User.Row(None, "test3@email.com", "Marcin", "Nowak")
    )

    val actions = for {
      _ <- User.Repository.create
      ids <- User.Repository saveAll users
      initialUsers <- User.Repository.findAll
      _ <- User.Repository.deleteAll()
      resultingUsers <- User.Repository.findAll
    } yield (initialUsers, resultingUsers)

    actions map {
      case (initialUsers, resultingUsers) =>
        initialUsers should have size users.size
        resultingUsers shouldBe empty
    }
  }

  it should "create and drop table" in runWithRollback {
    val actions = for {
      _ <- User.Repository.create
      _ <- User.Repository.drop
    } yield ()

    actions
  }
}

class CoreUserDSLRepositoryTest extends BaseTest[Long] with UserRepositoryDSLTest with DSLUserTable {
  override lazy val unicorn = TestUnicorn
  override val identifiers: Identifiers[Long] = LongUnicornIdentifiers
}