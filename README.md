Scala Slick type-safe ids
=========================

[![Join the chat at https://gitter.im/VirtusLab/unicorn](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/VirtusLab/unicorn?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/VirtusLab/unicorn.svg?branch=v0.6.x-slick-2.1.x)](https://travis-ci.org/VirtusLab/unicorn)
[![Coverage Status](https://coveralls.io/repos/github/VirtusLab/unicorn/badge.svg?branch=coveralls)](https://coveralls.io/github/VirtusLab/unicorn?branch=coveralls)

Slick (the Scala Language-Integrated Connection Kit) is a framework for type-safe, composable data access in Scala. This library adds tools to use type-safe IDs for your classes so you can no longer join on bad id field or mess up order of fields in mappings. It also provides a way to create data access layer with methods (like querying all, querying by id, saving or deleting) for all classes with such IDs in just 4 lines of code.

Idea for type-safe ids was derived from Slick creator's [presentation on ScalaDays 2013](http://www.parleys.com/play/51c2e20de4b0d38b54f46243/chapter63/about).

This library is used in [Advanced play-slick Typesafe Activator template](https://github.com/VirtusLab/activator-play-advanced-slick).


ScalaDoc API:
* [unicorn-core 1.1.0](http://virtuslab.github.io/unicorn/unicorn-v1.1.0/unicorn-core-api/)
* [unicorn-play 1.1.0](http://virtuslab.github.io/unicorn/unicorn-v1.1.0/unicorn-play-api/)

Unicorn is Open Source under [Apache 2.0 license](LICENSE).

Contributors
------------
* [Jerzy Müller](https://github.com/Kwestor)
* [Krzysztof Romanowski](https://github.com/romanowski)
* [Łukasz Dubiel](https://github.com/bambuchaAdm)
* [Matt Gilbert](https://github.com/mgilbertnz)
* [Paweł Batko](https://github.com/pbatko)
* [Krzysztof Borowski](https://github.com/liosedhel)
* [Paweł Dolega](https://github.com/pdolega)

Feel free to use it, test it and to contribute! For some helpful tips'n'tricks, see [contribution guide](CONTRIBUTING.md).

Getting unicorn
---------------

For core latest version (Scala 2.11.x and Slick 3.1.x) use:

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-core" % "1.1.0"
```

For play version (Scala 2.11.x, Slick 3.1.x, Play 2.5.x):

```scala
libraryDependencies += "org.virtuslab" %% "unicorn-play" % "1.1.0"
```

Or see [our Maven repository](http://maven-repository.com/artifact/org.virtuslab/).

For Slick 3.2.x and play 2.5 see version [`1.2.x`](https://github.com/VirtusLab/unicorn/tree/v1.2.x-slick-3.2.x)

For Slick 3.1.x and play 2.5 see version [`1.1.x`](https://github.com/VirtusLab/unicorn/tree/v1.1.x-slick-3.1.x)

For Slick 3.1.x and play 2.4 see version [`1.0.x`](https://github.com/VirtusLab/unicorn/tree/v1.0.x-slick-3.1.x)

For Slick 3.0.x see version [`0.7.x`](https://github.com/VirtusLab/unicorn/tree/v0.7.x-slick-3.0.x)

For Slick 2.1.x see version [`0.6.x`](https://github.com/VirtusLab/unicorn/tree/v0.6.x-slick-2.1.x)

For Slick 2.0.x see version [`0.5.x`](https://github.com/VirtusLab/unicorn/tree/v0.5.x-slick-2.0.x).

For Slick 1.x see version [`0.4.x`](https://github.com/VirtusLab/unicorn/tree/v0.4.x-slick-1.0.x).

Migration from older versions
=============================

See our [migration guide](MIGRATION.md).

Play Examples
=============

From version 0.5.0 forward dependency on Play! framework and `play-slick` library is no longer necessary.

If you are using Play! anyway, examples below show how to make use of `unicorn` then.

Defining entities
-----------------

```scala
package model

import org.virtuslab.unicorn.{BaseId, WithId}
import org.virtuslab.unicorn.LongUnicornPlayIdentifiers._

/** Id class for type-safe joins and queries. */
case class UserId(id: Long) extends AnyVal with BaseId[Long]

/** Companion object for id class, extends IdCompanion
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
case class UserRow(id: Option[UserId],
                   email: String,
                   firstName: String,
                   lastName: String) extends WithId[Long, UserId]
```

Defining composable repositories
--------------------------------

```scala
package repositories
/**
  * A place for all objects directly connected with database.
  *
  * Put your user queries here.
  * Having them in separate in this trait keeps `UserRepository` neat and tidy.
  */
trait UserBaseRepositoryComponent {
  self: UnicornWrapper[Long] =>
  import unicorn._
  import unicorn.driver.api._


  /** Table definition for users. */
  class Users(tag: Tag) extends IdTable[UserId, UserRow](tag, "USERS") {

    /** By definition id column is inserted as lowercase 'id',
      * if you want to change it, here is your setting.
      */
    protected override val idColumnName = "ID"

    def email = column[String]("EMAIL")

    def firstName = column[String]("FIRST_NAME")

    def lastName = column[String]("LAST_NAME")

    override def * = (id.?, email, firstName, lastName) <>(UserRow.tupled, UserRow.unapply)

  }

  class UserBaseRepository
    extends BaseIdRepository[UserId, UserRow, Users](TableQuery[Users])

  val userBaseRepository = new UserBaseRepository

}
@Singleton()
class UserRepository @Inject() (val unicorn: LongUnicornPlayJDBC)
  extends UserBaseRepositoryComponent with UnicornWrapper[Long] {

  import unicorn.driver.api._
  
  def save(user: UserRow): DBIO[UserId] = {
    userBaseRepository.save(user)
  }
}
```

Usage
-----

```scala
package repositories

import model.UserRow

import scala.concurrent.ExecutionContext.Implicits.global

class UsersRepositoryTest extends BasePlayTest with UserBaseRepositoryComponent {

  "Users Repository" should "save and query users" in runWithRollback {

    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")
    val action = for {
      _ <- userBaseRepository.create
      userId <- userBaseRepository.save(user)
      userOpt <- userBaseRepository.findById(userId)
    } yield userOpt

    action.map { userOpt =>
      userOpt.map(_.email) shouldEqual Some(user.email)
      userOpt.map(_.firstName) shouldEqual Some(user.firstName)
      userOpt.map(_.lastName) shouldEqual Some(user.lastName)
      userOpt.flatMap(_.id) should not be (None)
    }
  }
}
```

Core Examples
=============

If you do not want to include Play! but still want to use unicorn, `unicorn-core` will make it available for you.

Preparing Unicorn to work
-------------------------

First you have to bake your own cake to provide `unicorn` with proper driver (in example case H2),
as also build new object for Long ID support in entities:

```
package infra

object LongUnicornIdentifiers extends Identifiers[Long] {
  override def ordering: Ordering[Long] = implicitly[Ordering[Long]]

  override type IdCompanion[Id <: BaseId[Long]] = CoreCompanion[Id]
}

object Unicorn
    extends LongUnicornCore
    with HasJdbcDriver {

  override lazy val driver = H2Driver
}
```

Then you can use that cake to import driver and types provided by `unicorn` as shown in next sections.

Defining entities
-----------------

```scala
package model

import infra.LongUnicornIdentifiers._
import infra.Unicorn.driver.api._
import slick.lifted.Tag

/** Id class for type-safe joins and queries. */
case class UserId(id: Long) extends AnyVal with BaseId[Long]

/** Companion object for id class, extends IdMapping
  * and brings all required implicits to scope when needed.
  */
object UserId extends IdCompanion[UserId]

/** User entity.  */
case class UserRow(id: Option[UserId],
                email: String,
                firstName: String,
                lastName: String) extends WithId[Long, UserId]

/** Table definition for users. */
class Users(tag: Tag) extends IdTable[UserId, UserRow](tag, "USERS") {

  // use this property if you want to change name of `id` column to uppercase
  // you need this on H2 for example
  override val idColumnName = "ID"

  def email = column[String]("EMAIL")

  def firstName = column[String]("FIRST_NAME")

  def lastName = column[String]("LAST_NAME")

  override def * = (id.?, email, firstName, lastName) <> (UserRow.tupled, UserRow.unapply)
}
```

Defining repositories
---------------------

```scala
package repositories

import infra.Unicorn._
import infra.Unicorn.driver.api._
import model._

/**
 * Repository for users.
 *
 * It brings all base repository methods with it from [[BaseIdRepository]], but you can add yours as well.
 *
 * Use your favourite DI method to instantiate it in your application.
 */
class UsersRepository extends BaseIdRepository[UserId, UserRow, Users](TableQuery[Users])
```

Usage
-----

```scala
package repositories

import model.UserRow
import scala.concurrent.ExecutionContext.Implicits.global


class UsersRepositoryTest extends BaseTest[Long] {

  val usersRepository: UsersRepository = new UsersRepository

  "Users Service" should "save and query users" in runWithRollback {
    val user = UserRow(None, "test@email.com", "Krzysztof", "Nowak")

    val actions = for {
      _ <- usersRepository.create
      userId <- usersRepository.save(user)
      user <- usersRepository.findById(userId)
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
}
```

Defining custom underlying type
===============================

All reviews examples used `Long` as underlying `Id` type. From version `0.6.0` there is possibility to define own.

Let's use `String` as our type for `id`. So we should bake unicorn with `String` parametrization.

Play example
------------
```
@Singleton()
class StringUnicornPlay @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) extends UnicornPlay[String](dbConfig)

object StringUnicornPlayIdentifiers extends PlayIdentifiersImpl[String] {
  override val ordering: Ordering[String] = implicitly[Ordering[String]]
  override type IdCompanion[Id <: BaseId[String]] = PlayCompanion[Id]
}
```

Core example
------------ 
```
object StringUnicornIdentifiers extends Identifiers[String] {
  override def ordering: Ordering[String] = implicitly[Ordering[String]]

  override type IdCompanion[Id <: BaseId[Long]] = CoreCompanion[Id]
}

object Unicorn
    extends UnicornCore[String]
    with HasJdbcDriver {

  override lazy val driver = H2Driver
}
```

Usage is same as in `Long` example. Main difference is that you should import classes from self-baked cake.
The only concern is that `id` is auto-increment so we can't use arbitrary type there.
We plan to solve this problem in next versions.
