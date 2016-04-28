package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ LongTestUnicorn, TestUnicorn, BaseTest }
import TestUnicorn.driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class JunctionRepositoryTest extends BaseTest[Long] with LongTestUnicorn {

  import unicorn._

  behavior of classOf[JunctionRepository[_, _, _]].getSimpleName

  case class OrderId(id: Long) extends BaseId

  object OrderId extends IdCompanion[OrderId]

  case class CustomerId(id: Long) extends BaseId

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

  def createTables(implicit session: Session) = {
    OrderCustomerRepository.create
  }

  it should "save pairs" in rollback { implicit session =>
    createTables

    OrderCustomerRepository.save(OrderId(100), CustomerId(200))

    OrderCustomerRepository.findAll() should have size 1
  }

  it should "save pairs using action" in rollbackAction {
    val orderId = OrderId(100)
    val customerId = CustomerId(200)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      // WHEN
      _ <- OrderCustomerRepository.saveAction(orderId, customerId)
      // THEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      all should have size 1
    }
  }

  it should "save pair only once" in rollback { implicit session =>
    createTables

    OrderCustomerRepository.save(OrderId(100), CustomerId(200))
    OrderCustomerRepository.save(OrderId(100), CustomerId(200))

    OrderCustomerRepository.findAll() should have size 1
  }

  it should "save pair only once using Action" in rollbackAction {
    val orderId = OrderId(100)
    val customerId = CustomerId(200)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      // WHEN
      _ <- OrderCustomerRepository.saveAction(orderId, customerId)
      _ <- OrderCustomerRepository.saveAction(orderId, customerId)
      // THEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      all should have size 1
    }
  }

  it should "find all pairs" in rollback { implicit session =>
    createTables

    OrderCustomerRepository.save(OrderId(100), CustomerId(200))
    OrderCustomerRepository.save(OrderId(101), CustomerId(200))

    OrderCustomerRepository.findAll should have size 2
  }

  it should "find all pairs using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val customerId = CustomerId(200)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId)
      _ <- OrderCustomerRepository.saveAction(orderId2, customerId)
      // WHEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      // THEN
      all should have size 2
    }
  }

  it should "find by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    OrderCustomerRepository.save(orderId, CustomerId(200))
    OrderCustomerRepository.save(orderId, CustomerId(201))
    OrderCustomerRepository.save(OrderId(101), CustomerId(201))

    OrderCustomerRepository.forA(orderId) should have size 2
  }

  it should "find by first using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val customerId1 = CustomerId(200)
    val customerId2 = CustomerId(201)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId1)
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId2)
      _ <- OrderCustomerRepository.saveAction(orderId2, customerId2)
      // WHEN
      all <- OrderCustomerRepository.forAAction(orderId1)
    } yield {
      // THEN
      all should have size 2
    }
  }

  it should "find by second" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    OrderCustomerRepository.save(OrderId(100), customerId)
    OrderCustomerRepository.save(OrderId(101), customerId)
    OrderCustomerRepository.save(OrderId(101), CustomerId(100))

    OrderCustomerRepository.forB(customerId) should have size 2
  }

  it should "find by second using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val customerId1 = CustomerId(200)
    val customerId2 = CustomerId(201)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId1)
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId2)
      _ <- OrderCustomerRepository.saveAction(orderId2, customerId2)
      // WHEN
      all <- OrderCustomerRepository.forBAction(customerId2)
    } yield {
      // THEN
      all should have size 2
    }
  }

  it should "delete by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    OrderCustomerRepository.save(orderId, CustomerId(200))
    OrderCustomerRepository.save(orderId, CustomerId(201))

    OrderCustomerRepository.delete(orderId, CustomerId(200))

    OrderCustomerRepository.findAll() should have size 1
  }

  it should "delete by first using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val customerId1 = CustomerId(200)
    val customerId2 = CustomerId(201)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId1)
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId2)
      // WHEN
      _ <- OrderCustomerRepository.deleteAction(orderId1, customerId1)
      // THEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      all should have size 1
    }
  }

  it should "delete all items with given first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    OrderCustomerRepository.save(orderId, CustomerId(200))
    OrderCustomerRepository.save(orderId, CustomerId(201))

    OrderCustomerRepository.deleteForA(orderId)

    OrderCustomerRepository.findAll() shouldBe empty
  }

  it should "delete all items with given first using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val customerId1 = CustomerId(200)
    val customerId2 = CustomerId(201)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId1)
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId2)
      // WHEN
      _ <- OrderCustomerRepository.deleteForAAction(orderId1)
      // THEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      all shouldBe empty
    }
  }

  it should "delete all items with given second" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    OrderCustomerRepository.save(OrderId(100), customerId)
    OrderCustomerRepository.save(OrderId(101), customerId)

    OrderCustomerRepository.deleteForB(customerId)

    OrderCustomerRepository.findAll() shouldBe empty
  }

  it should "delete all items with given second using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val customerId1 = CustomerId(200)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId1)
      _ <- OrderCustomerRepository.saveAction(orderId2, customerId1)
      // WHEN
      _ <- OrderCustomerRepository.deleteForBAction(customerId1)
      // THEN
      all <- OrderCustomerRepository.findAllAction()
    } yield {
      all shouldBe empty
    }
  }

  it should "check that one pair exists" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    OrderCustomerRepository.save(OrderId(100), customerId)
    OrderCustomerRepository.save(OrderId(101), customerId)

    OrderCustomerRepository.exists(OrderId(200), customerId)(session) shouldBe false
    OrderCustomerRepository.exists(OrderId(101), customerId)(session) shouldBe true
  }

  it should "check that one pair exists using Action" in rollbackAction {
    val orderId1 = OrderId(100)
    val orderId2 = OrderId(101)
    val falseOrderId = OrderId(201)
    val customerId = CustomerId(200)

    for {
      // GIVEN
      _ <- OrderCustomerRepository.createAction()
      _ <- OrderCustomerRepository.saveAction(orderId1, customerId)
      _ <- OrderCustomerRepository.saveAction(orderId2, customerId)
      // WHEN
      first <- OrderCustomerRepository.existsAction(falseOrderId, customerId)
      second <- OrderCustomerRepository.existsAction(orderId1, customerId)
    } yield {
      // THEN
      first shouldBe false
      second shouldBe true
    }
  }

}
