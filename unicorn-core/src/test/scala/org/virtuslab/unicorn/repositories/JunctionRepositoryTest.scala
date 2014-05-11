package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.TestUnicorn
import TestUnicorn._
import TestUnicorn.driver.simple._
import org.virtuslab.unicorn.BaseTest

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

  //FIXME
  class OrderCustomerJunctionTable(tag: Tag) extends JunctionTable[OrderId, CustomerId](tag, "order_customer") {
    def orderId = column[OrderId]("ORDER_ID")
    def customerId = column[CustomerId]("CUSTOMER_ID")

    def columns = orderId -> customerId
  }

  val orderQueries = TableQuery[OrderTable]
  val customerQueries = TableQuery[CustomerTable]
  val junctionQueries = TableQuery[OrderCustomerJunctionTable]

  object exampleJunctionRepository extends JunctionRepository[OrderId, CustomerId, OrderCustomerJunctionTable](junctionQueries)

  "Junction table" should "link between tables" in rollback { implicit session =>
    orderQueries.ddl.create
    customerQueries.ddl.create
    junctionQueries.ddl.create

    val order = Order(None, 100)

    orderQueries += order
    val savedOrder = orderQueries.first

    val customer = Customer(None, "Ala", "ala@example.com")
    customerQueries += customer
    val savedCustomer = customerQueries.first

    exampleJunctionRepository.save(savedOrder.id.get, savedCustomer.id.get)

    junctionQueries.run should have size 1
  }
}
