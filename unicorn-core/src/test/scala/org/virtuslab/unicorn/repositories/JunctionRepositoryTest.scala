package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.TestUnicorn
import TestUnicorn._
import TestUnicorn.driver.simple._
import org.virtuslab.unicorn.BaseTest
import org.scalatest.BeforeAndAfterEach

/**
 * Created by Łukasz Dubiel on 25.03.14.
 */
class JunctionRepositoryTest extends BaseTest {

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

  it should "able to find by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    exampleJunctionRepository.save(orderId, CustomerId(200))
    exampleJunctionRepository.save(orderId, CustomerId(201))

    exampleJunctionRepository.forA(orderId) should have size 2
  }

  it should "able o find by second" in rollback { implicit session =>
    createTables
    val customerId = CustomerId(200)
    exampleJunctionRepository.save(OrderId(100), customerId)
    exampleJunctionRepository.save(OrderId(101), customerId)

    exampleJunctionRepository.forB(customerId) should have size 2
  }

  it should "able to delete by first" in rollback { implicit session =>
    createTables
    val orderId = OrderId(100)
    exampleJunctionRepository.save(orderId, CustomerId(200))
    exampleJunctionRepository.save(orderId, CustomerId(201))

    exampleJunctionRepository.delete(orderId, CustomerId(200))

    junctionQueries.run should have size 1
  }

}
