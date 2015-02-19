package org.virtuslab.unicorn.dsl

import org.virtuslab.unicorn.{ BaseTest, HasJdbcDriver, TestUnicorn, Unicorn }

class EntityDslTest extends BaseTest[Long] {

  val unicorn: Unicorn[Long] with HasJdbcDriver = TestUnicorn
  abstract class TestEntityDsl extends EntityDsl(unicorn)

  import unicorn.driver.simple._

  object User extends TestEntityDsl {

    case class Row(id: Option[Id],
      email: String,
      firstName: String,
      lastName: String) extends BaseRow

    class Table(tag: Tag) extends BaseTable(tag, "USERS") {
      def email = column[String]("EMAIL", O.NotNull)

      def firstName = column[String]("FIRST_NAME", O.NotNull)

      def lastName = column[String]("LAST_NAME", O.NotNull)

      override def * = (id.?, email, firstName, lastName) <> (Row.tupled, Row.unapply)
    }

    override val query = TableQuery[Table]
  }

  object UsersRepository extends User.BaseRepository

  // TODO - find a way to not duplicate whole UsersRepositoryTest

  "Users Service" should "save and query users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = User.Row(None, "test@email.com", "Krzysztof", "Nowak")
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

  it should "save and query multiple users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = (Stream from 1 take 10) map (n => User.Row(None, "test@email.com", "Krzysztof" + n, "Nowak"))
      UsersRepository saveAll users
      val newUsers = UsersRepository.findAll()
      newUsers.size shouldEqual 10
      newUsers.headOption map (_.firstName) shouldEqual Some("Krzysztof1")
      newUsers.lastOption map (_.firstName) shouldEqual Some("Krzysztof10")
  }

  it should "query existing user" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = User.Row(None, "test@email.com", "Krzysztof", "Nowak")
      val userId = UsersRepository save user
      val user2 = UsersRepository findExistingById userId

      user2 should have(
        'email(user.email),
        'firstName(user.firstName),
        'lastName(user.lastName)
      )
      user2.id shouldBe defined
  }

  it should "update existing user" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      var user = User.Row(None, "test@email.com", "Krzysztof", "Nowak")
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

  it should "query all ids" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
        User.Row(None, "test2@email.com", "Janek", "Nowak"),
        User.Row(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users

      UsersRepository.allIds() shouldEqual ids
  }

  it should "sort users by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
        User.Row(None, "test2@email.com", "Janek", "Nowak"),
        User.Row(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }

      UsersRepository.findAll().sortBy(_.id) shouldEqual usersWithIds
  }

  it should "query multiple users by ids" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
        User.Row(None, "test2@email.com", "Janek", "Nowak"),
        User.Row(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      UsersRepository.findAll().size shouldEqual 3

      val selectedUsers = Seq(usersWithIds.head, usersWithIds.last)

      UsersRepository.findByIds(selectedUsers.flatMap(_.id)) shouldEqual selectedUsers
  }

  it should "copy user by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val user = User.Row(None, "test1@email.com", "Krzysztof", "Nowak")

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

  it should "delete user by id" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
        User.Row(None, "test2@email.com", "Janek", "Nowak"),
        User.Row(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      val usersWithIds = (users zip ids).map { case (user, id) => user.copy(id = Some(id)) }
      UsersRepository.findAll() should have size users.size

      UsersRepository.deleteById(ids(1))
      val remainingUsers = Seq(usersWithIds.head, usersWithIds.last)

      UsersRepository.findAll() shouldEqual remainingUsers
  }

  it should "delete all users" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()

      val users = Seq(
        User.Row(None, "test1@email.com", "Krzysztof", "Nowak"),
        User.Row(None, "test2@email.com", "Janek", "Nowak"),
        User.Row(None, "test3@email.com", "Marcin", "Nowak")
      )

      val ids = UsersRepository saveAll users
      UsersRepository.findAll() should have size users.size

      UsersRepository.deleteAll()

      UsersRepository.findAll() shouldBe empty
  }

  it should "create and drop table" in rollback {
    implicit session =>
      // setup
      UsersRepository.create()
      UsersRepository.drop()
  }
}
