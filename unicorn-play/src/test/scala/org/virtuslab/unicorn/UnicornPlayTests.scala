package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.DSLUserTable
import org.virtuslab.unicorn.repositories.UserRepositoryDSLTest
import org.virtuslab.unicorn.repositories.{ AbstractUserTable, UsersRepositoryTest }

class UnicornPlayTests
    extends BasePlayTest
    with UsersRepositoryTest
    with AbstractUserTable {
  override val identifiers: Identifiers[Long] = LongUnicornPlayIdentifiers
}

class UnicornPlayDSLTests
    extends BasePlayTest
    with UserRepositoryDSLTest
    with DSLUserTable {
  override val identifiers: Identifiers[Long] = LongUnicornPlayIdentifiers
}