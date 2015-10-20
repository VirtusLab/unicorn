package org.virtuslab.unicorn

import play.api.data.format.Formats._
import slick.lifted.{ ProvenShape, Tag => SlickTag }

object StringPlayUnicorn extends UnicornPlay[String]

import StringPlayUnicorn._
import StringPlayUnicorn.driver.api._

case class UserId(id: String) extends BaseId

object UserId extends IdCompanion[UserId]

case class UserRow(id: Option[UserId], name: String) extends WithId[UserId]

class UserTable(tag: SlickTag) extends IdTable[UserId, UserRow](tag, "test") {
  def name = column[String]("name")
  override def * : ProvenShape[UserRow] = (id.?, name) <> (UserRow.tupled, UserRow.unapply)
}