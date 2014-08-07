package org.virtuslab.unicorn

import org.virtuslab.unicorn.repositories.{ UsersRepositoryTest, AbstractUserTable }

class UnicornPlayTests
  extends BasePlayTest
  with UsersRepositoryTest
  with AbstractUserTable
