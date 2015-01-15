Scala Slick type-safe ids
=========================

[![Join the chat at https://gitter.im/VirtusLab/unicorn](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/VirtusLab/unicorn?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/VirtusLab/unicorn.svg?branch=v0.6.x-slick-2.1.x)](https://travis-ci.org/VirtusLab/unicorn)
[![Coverage Status](https://img.shields.io/coveralls/VirtusLab/unicorn.svg)](https://coveralls.io/r/VirtusLab/unicorn?branch=v0.6.x-slick-2.1.x)

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

For core latest version (Scala 2.10.x/2.11.x and Slick 2.1.x) use:

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-core" % "0.6.2"
```

For play version (Scala 2.10.x/2.11.x, Slick 2.1.x, Play 2.3.x):

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-play" % "0.6.2"
```

Or see [our Maven repository](http://maven-repository.com/artifact/org.virtuslab/).

For Slick 2.0.x see version [`0.5.x`](https://github.com/VirtusLab/unicorn/tree/v0.5.x-slick-2.0.x).

For Slick 1.x see version [`0.4.x`](https://github.com/VirtusLab/unicorn/tree/v0.4.x-slick-1.0.x).

Migration form 0.5.x to 0.6.x
=============================

Version 0.6.x brings possibility for using different type then `Long` as underlying `Id` type.
The most interesting are `UUID` and `String`. This change allow us to start working on typesafe composite keys.

For backward compatibility with `0.5.x` we introduced `LongUnicornCore` and `LongUnicornPlay`.
Before attempting to perform this migration you should known how to migrate your tables definitions to `slick-2.1.x`.
All needed information you could find in awesome [migration guide](http://slick.typesafe.com/doc/2.1.0/upgrade.html#upgrade-from-2-0-to-2-1) 


Core migration
--------------

Changes is only on backing your version on unicorn cake. So code like:
```
object Unicorn extends UnicornCore with HasJdbcDriver {
  val driver = H2Driver
}
```
now becomes:
```
object Unicorn extends LongUnicornCore with HasJdbcDriver {
  val driver = H2Driver
}
```
and this is all your changes.

Play migration
--------------

When you use `unicorn-play` migration is still quite easy,
but it will touch all file where unicorn and slick was used.
 
Imports like those:
```
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
```
now becomes:
```
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
```

Play Examples
=============

From version 0.5.0 forward dependency on Play! framework and `play-slick` library is no longer necessary.

If you are using Play! anyway, examples below shows how to make use of `unicorn` then.

Defining entities
-----------------

```scala
package model

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
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

  // use this property if you want to change name of `id` column to uppercase
  // you need this on H2 for example
  override val idColumnName = "ID"

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
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

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

import org.virtuslab.unicorn.{HasJdbcDriver, LongUnicornCore}
import scala.slick.driver.H2Driver

object Unicorn extends LongUnicornCore with HasJdbcDriver {
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

  // use this property if you want to change name of `id` column to uppercase
  // you need this on H2 for example
  override val idColumnName = "ID"

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

Defining custom underlying type
===============================

All reviews examples used `Long` as underlying `Id` type. From version `0.6.0` there is possibility to define own.

Let's use `String` as our type for `id`. So we should bake unicorn with `String` parametrization.

Play example
------------
```
object StringPlayUnicorn extends UnicornPlay[String]
```

Core example
------------ 
```
object StringUnicorn extends UnicornCore[String] with HasJdbcDriver {
  override val driver = H2Driver
}
```

Usage is same as in `Long` example. Main difference is that you should import classes from self-baked cake.
The only concern is that `id` is auto-increment so we can't use arbitrary type there.
We plan to solve this problem in next versions.
