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

Feel free to use it, test it and to contribute!

Getting unicorn
---------------

For latest version (Scala 2.10.x compatible) use:

```scala
libraryDependencies += "org.virtuslab" %% "unicorn" % "0.4.1"
```

Or see [Maven repository](http://maven-repository.com/artifact/org.virtuslab/unicorn_2.10/0.4), currently latest published is 0.4.

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
object Users extends IdTable[UserId, User]("users") {

  def email = column[String]("email", O.NotNull)

  def firstName = column[String]("first_name", O.NotNull)

  def lastName = column[String]("last_name", O.NotNull)

  def base = email ~ firstName ~ lastName

  override def * = id.? ~: base <> (User.apply _, User.unapply _)

  override def insertOne(elem: User)(implicit session: Session): UserId =
    saveBase(base, User.unapply _)(elem)
}
```

Defining services
-----------------

```scala
package service

import model._
import play.api.db.slick.Config.driver.simple._
import org.virtuslab.unicorn.ids.services._

/**
 * Queries for users.
 * It brings all base queries with it from [[service.BaseIdQueries]], but you can add yours as well.
 */
trait UsersQueries extends BaseIdQueries[UserId, User] {
  override def table = Users
}

/**
 * Service for users.
 *
 * It brings all base service methods with it from [[service.BaseIdService]], but you can add yours as well.
 *
 * It's a trait, so you can use your favourite DI method to instantiate/mix it to your application.
 */
trait UsersService extends BaseIdService[UserId, User] with UsersQueries
```

Usage
-----

```scala
package service

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import play.api.db.slick.DB
import model.User
import scala.slick.session.Session

class UsersServiceTest extends Specification {

  "Users Service" should {

    "save and query users in" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          object UsersService extends UsersService

          val user = User(None, "test@email.com", "Krzysztof", "Nowak")
          val userId = UsersService save user
          val userOpt = UsersService findById userId

          userOpt.map(_.email) must be_=== (Some(user.email))
          userOpt.map(_.firstName) must be_=== (Some(user.firstName))
          userOpt.map(_.lastName) must be_=== (Some(user.lastName))
          userOpt.flatMap(_.id) must not be_=== None
      }
    }
  }
}
```
