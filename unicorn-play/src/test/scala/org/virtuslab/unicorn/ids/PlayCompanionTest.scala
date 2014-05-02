package org.virtuslab.unicorn.ids

import org.virtuslab.unicorn.BasePlayTest
import org.virtuslab.unicorn.ids.UnicornPlay._
import play.api.data.format.Formatter
import play.api.mvc.{ PathBindable, QueryStringBindable }

class PlayCompanionTest extends BasePlayTest {

  case class UserId(id: Long) extends BaseId

  object UserId extends IdCompanion[UserId]

  it should "have implicit query string binder" in {
    implicitly[QueryStringBindable[UserId]] shouldNot be(null)
  }

  it should "have implicit formatter" in {
    implicitly[Formatter[UserId]] shouldNot be(null)
  }

  it should "have implciit path bindable" in {
    implicitly[PathBindable[UserId]] shouldNot be(null)
  }
}
