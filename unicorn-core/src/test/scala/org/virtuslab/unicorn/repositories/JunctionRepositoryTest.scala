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

  case class Order(id: Option[OrderId], quantity: Int) extends WithId[OrderId]

  class OrderTable(tag: Tag) extends IdTable[OrderId, Order](tag, "ORDERS") {

    def quantity = column[Int]("quantity")

    override def * = (id.?, quantity) <> (Order.tupled, Order.unapply)
  }

  case class CustomerId(id: Long) extends BaseId

  object CustomerId extends IdCompanion[CustomerId]

  case class Customer(id: Option[CustomerId], name: String, email: String) extends WithId[CustomerId]

  class CustomerTable(tag: Tag) extends IdTable[CustomerId, Customer](tag, "CUSTOMER") {
    def name = column[String]("name")
    def email = column[String]("email")
    def * = (id.?, name, email) <> (Customer.tupled, Customer.unapply)
  }

  class OrderCustomerJunctionTable(tag: Tag) extends JunctionTable[OrderId, CustomerId](tag, "order_customer") {
    def orderId = column[OrderId]("ORDER_ID")
    def customerId = column[CustomerId]("CUSTOMER_ID")

    def columns = orderId -> customerId
  }

  val orderQueries = TableQuery[OrderTable]
  val customerQueries = TableQuery[CustomerTable]
  val junctionQueries = TableQuery[OrderCustomerJunctionTable]

  object exampleJunctionRepository extends JunctionRepository[OrderId, CustomerId, OrderCustomerJunctionTable](junctionQueries)

  val order = Order(None, 100)
  val customer = Customer(None, "Ala", "ala@example.com")

  def createTables(implicit session: Session) = {
    orderQueries.ddl.create
    customerQueries.ddl.create
    junctionQueries.ddl.create
  }

  def addExampleEntry(implicit session: Session): (OrderId, CustomerId) = {
    orderQueries += order
    val savedOrder = orderQueries.first

    customerQueries += customer
    val savedCustomer = customerQueries.first

    exampleJunctionRepository.save(savedOrder.id.get, savedCustomer.id.get)

    (orderQueries.first.id.get, customerQueries.first.id.get)
  }

  it should "save pairs" in rollback { implicit session =>
    createTables
    addExampleEntry

    junctionQueries.run should have size 1
  }

  it should "able to find by first" in rollback { implicit session =>
    createTables
    val (orderId, _) = addExampleEntry

    exampleJunctionRepository.forA(orderId) shouldNot have size 0

  }

}
