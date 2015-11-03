package org.virtuslab.unicorn

import LongUnicornPlay._
import org.virtuslab.unicorn.repositories.AbstractUserTable

class SyncRepTest extends BasePlayTest with AbstractUserTable {

  it should "use implicit .list method on Query" in rollback { implicit session =>
    // setup
    UsersRepository.create()

    (for (user <- usersQuery) yield user).list shouldBe Vector()
  }

  it should "use implicit .run method on Query" in rollback { implicit session =>
    // setup
    UsersRepository.create()

    (for (user <- usersQuery) yield user).run shouldBe Vector()
  }

}
