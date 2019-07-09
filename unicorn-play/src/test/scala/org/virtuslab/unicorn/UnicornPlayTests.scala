package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.{ AbstractUserTable, UsersRepositoryTest }

class UnicornPlayTests
  extends BasePlayTest
  with UsersRepositoryTest
  with AbstractUserTable {
  override val identifiers: Identifiers[Long] = LongUnicornPlayIdentifiers
}
