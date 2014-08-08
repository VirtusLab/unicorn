package org.virtuslab.unicorn

import play.api.data.format.Formats._

import scala.slick.lifted.ProvenShape

object StringPlayUnicorn extends UnicornPlay[String]

import org.virtuslab.unicorn.StringPlayUnicorn._
import org.virtuslab.unicorn.StringPlayUnicorn.driver.simple.{ Tag => SlickTag, _ }

case class UserId(id: String) extends BaseId

object UserId extends IdCompanion[UserId]

case class UserRow(id: Option[UserId], name: String) extends WithId[UserId]

class UserTable(tag: SlickTag) extends IdTable[UserId, UserRow](tag, "test") {
  def name = column[String]("name")
  override def * : ProvenShape[UserRow] = (id.?, name) <> (UserRow.tupled, UserRow.unapply)
}