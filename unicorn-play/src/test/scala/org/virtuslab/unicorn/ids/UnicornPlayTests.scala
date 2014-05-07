package org.virtuslab.unicorn.ids

import org.virtuslab.unicorn.ids.repositories.{ UsersRepositoryTest, AbstractUserTable }
import org.virtuslab.unicorn.BasePlayTest

class UnicornPlayTests extends BasePlayTest with UsersRepositoryTest with AbstractUserTable
