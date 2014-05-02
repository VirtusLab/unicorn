Scala Slick type-safe ids
=========================

Slick (the Scala Language-Integrated Connection Kit) is a framework for type-safe, composable data access in Scala. This library adds tools to use type-safe IDs for your classes so you can no longer join on bad id field or mess up order of fields in mappings. It also provides a way to create service layer with methods (like querying all, querying by id, saving or deleting) for all classes with such IDs in just 4 lines of code.

Idea for type-safe ids was derived from Slick creator's [presentation on ScalaDays 2013](http://www.parleys.com/play/51c2e20de4b0d38b54f46243/chapter63/about).

This library is used in [Advanced play-slick Typesafe Activator template](https://github.com/VirtusLab/activator-play-advanced-slick).

Contributors
------------
Authors:
* [Jerzy Müller](https://github.com/Kwestor)
* [Krzysztof Romanowski](https://github.com/romanowski)
* [Łukasz Dubiel](https://github.com/bambuchaAdm)

Feel free to use it, test it and to contribute!

Getting unicorn
---------------

For latest version (for Scala 2.10.x and Slick 2.0) use:

```scala
libraryDependencies += "org.virtuslab" %% "unicorn" % "0.5.0-RC1"
```

Or see [Maven repository](http://maven-repository.com/artifact/org.virtuslab/unicorn_2.10).

For Slick 1.x see version `0.4.x`.

Examples
========

Defining entities
-----------------

```scala
package model

import scala.slick.session.Session
import org.virtuslab.unicorn.ids._

/** Id class for type-safe joins and queries. */
case class UserId(id: Long) extends AnyVal with BaseId

/** Companion object for id class, extends IdMapping
  * and brings all required implicits to scope when needed.
  */
object UserId extends IdCompanion[UserId]

/** User entity.
  *
  * @param id user id
  * @param email user email address
  * @param lastName lastName
  * @param firstName firstName
  */
case class User(id: Option[UserId],
                email: String,
                firstName: String,
                lastName: String) extends WithId[UserId]

/** Table definition for users. */
class Users(tag: Tag) extends IdTable[UserId, User](tag, "USERS") {

  def email = column[String]("EMAIL", O.NotNull)

  def firstName = column[String]("FIRST_NAME", O.NotNull)

  def lastName = column[String]("LAST_NAME", O.NotNull)

  override def * = (id.?, email, firstName, lastName) <> (User.tupled, User.unapply)
}
```

Defining repositories
---------------------

```scala
package repositories

import model._
import play.api.db.slick.Config.driver.simple._
import org.virtuslab.unicorn.ids.repositories._

/**
 * Repository for users.
 *
 * It brings all base service methods with it from [[service.BaseIdRepository]], but you can add yours as well.
 *
 * Use your favourite DI method to instantiate it in your application.
 */
class UsersRepository extends BaseIdRepository[UserId, User, Users]("USERS", TableQuery[Users])
```

Usage
-----

```scala
package repositories

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import play.api.db.slick.DB
import model.User
import scala.slick.session.Session

class UsersRepositoryTest extends AppTest {

  "Users repository" should "save and query users" in rollback { implicit session =>
    // setup
    val repository = new UsersRepository
    usersQuery.ddl.create

    val user = User(None, "test@email.com", "Krzysztof", "Nowak")
    val userId = UsersRepository save user
    val userOpt = UsersRepository findById userId

    userOpt.map(_.email) shouldEqual Some(user.email)
    userOpt.map(_.firstName) shouldEqual Some(user.firstName)
    userOpt.map(_.lastName) shouldEqual Some(user.lastName)
    userOpt.flatMap(_.id) shouldNot be(None)
  }
}
```
