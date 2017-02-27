package org.virtuslab.unicorn

import com.google.inject.{ Inject, Singleton }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.{ ProvenShape, Tag => SlickTag }
import play.api.data.format.Formats._

@Singleton()
class StringUnicornPlay @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) extends UnicornPlay[String](dbConfig)

object StringUnicornPlayIdentifiers extends PlayIdentifiersImpl[String] {
  override val ordering: Ordering[String] = implicitly[Ordering[String]]
  override type IdCompanion[Id <: BaseId[String]] = PlayCompanion[Id]
}

import StringUnicornPlayIdentifiers._

case class UserId(id: String) extends BaseId[String]

object UserId extends IdCompanion[UserId]

case class UserRow(id: Option[UserId], name: String) extends WithId[String, UserId]

trait UserQuery {
  self: UnicornWrapper[String] =>

  import unicorn._
  import unicorn.profile.api._

  class UserTable(tag: SlickTag) extends IdTable[UserId, UserRow](tag, "test") {
    def name = column[String]("name")
    override def * : ProvenShape[UserRow] = (id.?, name) <> (UserRow.tupled, UserRow.unapply)
  }

  class UserRepository extends BaseIdRepository[UserId, UserRow, UserTable](TableQuery[UserTable])
}

