Scala Slick type-safe ids
=========================
[![Build Status](https://travis-ci.org/VirtusLab/unicorn.svg?branch=master)](https://travis-ci.org/VirtusLab/unicorn)

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

For core latest version (for Scala 2.10.x and Slick 2.1.x) use:

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-core" % "0.6.0-M3"
```

For play version (same Scala 2.10.x and Slick 2.1.x):

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-play" % "0.6.0-M3"
```

Or see [Maven repository](http://maven-repository.com/artifact/org.virtuslab/unicorn_2.10).

For Slick 2.0.x see version `0.5.x`.

For Slick 1.x see version `0.4.x`.

Play Examples
=============

From version 0.5.0 forward dependency on Play! framework and `play-slick` library is no longer necessary.

If you are using Play! anyway, examples below shows how to make use of `unicorn` then.

Defining entities
-----------------

```scala
package model

import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import scala.slick.lifted.Tag

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
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._

/**
 * Repository for users.
 *
 * It brings all base service methods with it from [[BaseIdRepository]], but you can add yours as well.
 *
 * Use your favourite DI method to instantiate it in your application.
 */
class UsersRepository extends BaseIdRepository[UserId, User, Users](TableQuery[Users])
```

Usage
-----

```scala
package repositories

import model.User
import org.virtuslab.unicorn.BasePlayTest

class UsersRepositoryTest extends BasePlayTest {

  "Users repository" should "save and query users" in rollback { implicit session =>
    // setup
    val repository = new UsersRepository
    repository.create

    val user = User(None, "test@email.com", "Krzysztof", "Nowak")
    val userId = repository save user
    val userOpt = repository findById userId

    userOpt.map(_.email) shouldEqual Some(user.email)
    userOpt.map(_.firstName) shouldEqual Some(user.firstName)
    userOpt.map(_.lastName) shouldEqual Some(user.lastName)
    userOpt.flatMap(_.id) shouldNot be(None)
  }
}
```

Core Examples
=============

If you do not want to include Play! but still want to use unicorn, `unicorn-core` will make it available for you.

Preparing Unicorn to work
-------------------------

First you have to bake your own cake to provide `unicorn` with proper driver (in example case H2):

```
package com.example

import org.virtuslab.unicorn.{HasJdbcDriver, UnicornCore}
import scala.slick.driver.H2Driver

object Unicorn extends UnicornCore with HasJdbcDriver {
  val driver = H2Driver
}
```

Then you can use that cake to import driver and types provided by `unicorn` as shown in next sections.

Defining entities
-----------------

```scala
package com.example.tables

import package com.example.Unicorn._
import package com.example.Unicorn.driver.simple._

/** Id class for type-safe joins and queries. */
case class UserId(id: Long) extends AnyVal with BaseId

/** Companion object for id class and ordering fpr Id */
object UserId extends IdCompanion[UserId]

/** User entity.
  *
  * @param id user id
  * @param email user email address
  * @param lastName lastName
  * @param firstName firstName
  */
case class UserRow(id: Option[UserId],
                   email: String,
                   firstName: String,
                   lastName: String) extends WithId[UserId]

/** Table definition for users. */
class Users(tag: Tag) extends IdTable[UserId, UserRow](tag, "USERS") {

  def email = column[String]("EMAIL", O.NotNull)

   def firstName = column[String]("FIRST_NAME", O.NotNull)

  def lastName = column[String]("LAST_NAME", O.NotNull)

  override def * = (id.?, email, firstName, lastName) <> (UserRow.tupled, UserRow.unapply)
}
```

Defining repositories
---------------------

```scala
package com.example.repositories

import com.example.tables._
import play.api.db.slick.Config.driver.simple._
import org.virtuslab.unicorn.repositories._

/**
 * Repository for users.
 *
 * It brings all base service methods with it from [[service.BaseIdRepository]], but you can add yours as well.
 *
 * Use your favourite DI method to instantiate it in your application.
 */
class UsersRepository extends BaseIdRepository[UserId, UserRow, Users](TableQuery[Users])
```

Usage
-----

```scala
package com.example.test

import com.example.repositories.UserRepository

class UsersRepositoryTest extends BaseTest {

  val userRepository = new UserRepository

  "Users Service" should "save and query users" in rollback { implicit session =>
    // setup
    usersQuery.ddl.create

    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
    val userId = userRepository save user
    val userOpt = userRepository findById userId

    userOpt.map(_.email) shouldEqual Some(user.email)
    userOpt.map(_.firstName) shouldEqual Some(user.firstName)
    userOpt.map(_.lastName) shouldEqual Some(user.lastName)
    userOpt.flatMap(_.id) shouldNot be(None)
  }
}
```

