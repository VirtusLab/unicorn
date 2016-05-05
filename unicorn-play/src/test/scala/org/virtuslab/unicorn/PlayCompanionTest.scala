package org.virtuslab.unicorn

import LongUnicornPlayIdentifiers._
import play.api.data.format.Formatter
import play.api.mvc.{ PathBindable, QueryStringBindable }
import play.api.libs.json._

class PlayCompanionTest extends BasePlayTest {

  case class UserId(id: Long) extends BaseId[Long]

  object UserId extends IdCompanion[UserId]

  case class User(id: UserId, name: String)

  it should "have implicit query string binder" in {
    implicitly[QueryStringBindable[UserId]] shouldNot be(null)
  }

  it should "have implicit json format" in {
    implicitly[Format[UserId]] shouldNot be(null)
  }

  it should "have implicit formatter" in {
    implicitly[Formatter[UserId]] shouldNot be(null)
  }

  it should "have implicit path bindable" in {
    implicitly[PathBindable[UserId]] shouldNot be(null)
  }

  it should "have working implicit query string binder" in {
    val qsb = implicitly[QueryStringBindable[UserId]]
    val userId = UserId(123)
    qsb.bind("id", Map("id" -> Seq("123"))).value shouldEqual Right(userId)
    qsb.unbind("id", userId) shouldEqual "id=123"
  }

  it should "have working implicit json format" in {
    import play.api.libs.functional.syntax._

    val user = User(UserId(123), "Jerzy")
    val jsonUser = Json.parse(""" { "id" : 123, "name": "Jerzy" } """)

    // Reads

    implicit val reads: Reads[User] = (
      (JsPath \ "id").read[UserId] and
      (JsPath \ "name").read[String]
    )(User.apply _)

    jsonUser.validate[User].get shouldEqual user

    // Writes

    implicit val writes: Writes[User] = (
      (JsPath \ "id").write[UserId] and
      (JsPath \ "name").write[String]
    )(unlift(User.unapply))

    Json.toJson(user) shouldEqual jsonUser
  }

  it should "have working implicit formatter" in {
    import play.api.data._
    import play.api.data.Forms._

    val user = User(UserId(123), "Jerzy")
    val userMap = Map("id" -> "123", "name" -> "Jerzy")

    val userForm = Form(
      mapping(
        "id" -> Forms.of[UserId],
        "name" -> text
      )(User.apply)(User.unapply)
    )

    // Reads
    userForm.bind(userMap).get shouldEqual user

    // Writes - just check if it works without errors
    userForm.fill(user)

    // Test erroneous input
    val emptyBadUserMap = Map("id" -> "", "name" -> "Jerzy")
    val numberFormatBadUserMap = Map("id" -> "123a", "name" -> "Jerzy")

    userForm.bind(emptyBadUserMap).errors shouldEqual List(FormError("id", Seq("id.empty")))
    userForm.bind(numberFormatBadUserMap).errors shouldEqual List(FormError("id", Seq("id.invalid")))
  }

  it should "have working implicit path bindable" in {
    val pb = implicitly[PathBindable[UserId]]
    val userId = UserId(123)
    val userIdString = "123"

    pb.bind("id", userIdString) shouldEqual Right(userId)
    pb.unbind("id", userId) shouldEqual userIdString
  }
}
