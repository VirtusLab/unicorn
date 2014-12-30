package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ LongTestUnicorn, TestUnicorn, BaseTest }
import TestUnicorn._
import TestUnicorn.driver.simple._

class JunctionRepositoryTest extends BaseTest[Long] with LongTestUnicorn {

  import unicorn._

  behavior of classOf[JunctionRepository[_, _, _]].getSimpleName

  case class OrderId(id: Long) extends BaseId

  object OrderId extends IdCompanion[OrderId]

  case class CustomerId(id: Long) extends BaseId

  object CustomerId extends IdCompanion[CustomerId]

  class OrderCustomerJunctionTable(tag: Tag) extends JunctionTable[OrderId, CustomerId](tag, "order_customer") {
    def orderId = column[OrderId]("ORDER_ID")
    def customerId = column[CustomerId]("CUSTOMER_ID")

    def columns = orderId -> customerId
  }

  val junctionQueries = TableQuery[OrderCustomerJunctionTable]

  object exampleJunctionRepository extends JunctionRepository[OrderId, CustomerId, OrderCustomerJunctionTable](junctionQueries)

  def createTables(implicit session: Session) = {
    junctionQueries.ddl.create
  }

  it should "save pairs" in rollback { implicit session =>
    createTables

    exampleJunctionRepository.save(OrderId(100), CustomerId(200))

    junctionQueries.run should have size 1
  }

  it should "save pair only once" in rollback { implicit session =>
    createTables

    exampleJunctionRepository.save(OrderId(100), CustomerId(200))
    exampleJunctionRepository.save(OrderId(100), CustomerId(200))

    junctionQueries.run should have size 1
  }

  it should "find all pairs" in rollback { implicit session =>
    createTables

    junctionQueries += ((OrderId(100), CustomerId(200)))
    junctionQueries += ((OrderId(101), CustomerId(200)))

    exampleJunctionRepository.findAll should have size 2
  }

  it should "find by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    exampleJunctionRepository.save(orderId, CustomerId(200))
    exampleJunctionRepository.save(orderId, CustomerId(201))
    exampleJunctionRepository.save(OrderId(101), CustomerId(201))

    exampleJunctionRepository.forA(orderId) should have size 2
  }

  it should "find by second" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    exampleJunctionRepository.save(OrderId(100), customerId)
    exampleJunctionRepository.save(OrderId(101), customerId)
    exampleJunctionRepository.save(OrderId(101), CustomerId(100))

    exampleJunctionRepository.forB(customerId) should have size 2
  }

  it should "delete by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    exampleJunctionRepository.save(orderId, CustomerId(200))
    exampleJunctionRepository.save(orderId, CustomerId(201))

    exampleJunctionRepository.delete(orderId, CustomerId(200))

    junctionQueries.run should have size 1
  }

  it should "delete all items with given first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    exampleJunctionRepository.save(orderId, CustomerId(200))
    exampleJunctionRepository.save(orderId, CustomerId(201))

    exampleJunctionRepository.deleteForA(orderId)

    junctionQueries.run shouldBe empty
  }

  it should "delete all items with given second" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    exampleJunctionRepository.save(OrderId(100), customerId)
    exampleJunctionRepository.save(OrderId(101), customerId)

    exampleJunctionRepository.deleteForB(customerId)

    junctionQueries.run shouldBe empty
  }

  it should "check that one pair exists" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    exampleJunctionRepository.save(OrderId(100), customerId)
    exampleJunctionRepository.save(OrderId(101), customerId)

    exampleJunctionRepository.exists(OrderId(200), customerId)(session) shouldBe false
    exampleJunctionRepository.exists(OrderId(101), customerId)(session) shouldBe true
  }

}
