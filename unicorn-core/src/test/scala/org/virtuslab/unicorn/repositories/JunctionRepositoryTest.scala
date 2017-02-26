package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.LongUnicornIdentifiers.IdCompanion
import org.virtuslab.unicorn.TestUnicorn.profile.api._
import org.virtuslab.unicorn.{ BaseId, BaseTest, LongTestUnicorn }

import scala.concurrent.ExecutionContext.Implicits.global

class JunctionRepositoryTest extends BaseTest[Long] with LongTestUnicorn {

  import unicorn._

  behavior of classOf[JunctionRepository[_, _, _]].getSimpleName

  case class OrderId(id: Long) extends BaseId[Long]

  object OrderId extends IdCompanion[OrderId]

  case class CustomerId(id: Long) extends BaseId[Long]

  object CustomerId extends IdCompanion[CustomerId]

  class OrderCustomer(tag: Tag) extends JunctionTable[OrderId, CustomerId](tag, "order_customer") {
    def orderId = column[OrderId]("ORDER_ID")

    def customerId = column[CustomerId]("CUSTOMER_ID")

    def columns = orderId -> customerId
  }

  object OrderCustomer {
    val tableQuery = TableQuery[OrderCustomer]
  }

  object OrderCustomerRepository
    extends JunctionRepository[OrderId, CustomerId, OrderCustomer](OrderCustomer.tableQuery)

  it should "save pairs" in runWithRollback {
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), CustomerId(200))
      all <- OrderCustomerRepository.findAll()
    } yield all

    actions map { result =>
      result should have size 1
    }
  }

  it should "save pair only once" in runWithRollback {
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), CustomerId(200))
      _ <- OrderCustomerRepository.save(OrderId(100), CustomerId(200))
      all <- OrderCustomerRepository.findAll
    } yield all

    actions map { result =>
      result should have size 1
    }
  }

  it should "find all pairs" in runWithRollback {
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), CustomerId(200))
      _ <- OrderCustomerRepository.save(OrderId(101), CustomerId(200))
      all <- OrderCustomerRepository.findAll
    } yield all

    actions map { result =>
      result should have size 2
    }
  }

  it should "find by first" in runWithRollback {
    val orderId = OrderId(100)
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(orderId, CustomerId(200))
      _ <- OrderCustomerRepository.save(orderId, CustomerId(201))
      _ <- OrderCustomerRepository.save(OrderId(101), CustomerId(201))
      order <- OrderCustomerRepository.forA(orderId)
    } yield order

    actions map { result =>
      result should have size 2
    }
  }

  it should "find by second" in runWithRollback {
    val customerId = CustomerId(200)
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), customerId)
      _ <- OrderCustomerRepository.save(OrderId(101), customerId)
      _ <- OrderCustomerRepository.save(OrderId(101), CustomerId(100))
      order <- OrderCustomerRepository.forB(customerId)
    } yield order

    actions map { result =>
      result should have size 2
    }
  }

  it should "delete by first" in runWithRollback {
    val orderId = OrderId(100)
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(orderId, CustomerId(200))
      _ <- OrderCustomerRepository.save(orderId, CustomerId(201))
      _ <- OrderCustomerRepository.delete(orderId, CustomerId(200))
      orders <- OrderCustomerRepository.findAll()
    } yield orders

    actions map { result =>
      result should have size 1
    }
  }

  it should "delete all items with given first" in runWithRollback {
    val orderId = OrderId(100)
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(orderId, CustomerId(200))
      _ <- OrderCustomerRepository.save(orderId, CustomerId(201))
      _ <- OrderCustomerRepository.deleteForA(orderId)
      orders <- OrderCustomerRepository.findAll
    } yield orders

    actions map { result =>
      result shouldBe empty
    }
  }

  it should "delete all items with given second" in runWithRollback {
    val customerId = CustomerId(200)
    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), customerId)
      _ <- OrderCustomerRepository.save(OrderId(101), customerId)
      _ <- OrderCustomerRepository.deleteForB(customerId)
      all <- OrderCustomerRepository.findAll
    } yield all

    actions map { result =>
      result shouldBe empty
    }
  }

  it should "check that one pair exists" in runWithRollback {
    val customerId = CustomerId(200)

    val actions = for {
      _ <- OrderCustomerRepository.create
      _ <- OrderCustomerRepository.save(OrderId(100), customerId)
      _ <- OrderCustomerRepository.save(OrderId(101), customerId)
      firstExist <- OrderCustomerRepository.exists(OrderId(200), customerId)
      secondExist <- OrderCustomerRepository.exists(OrderId(101), customerId)
    } yield (firstExist, secondExist)

    actions map {
      case (firstExist, secondExist) =>
        firstExist shouldBe false
        secondExist shouldBe true
    }
  }

}
