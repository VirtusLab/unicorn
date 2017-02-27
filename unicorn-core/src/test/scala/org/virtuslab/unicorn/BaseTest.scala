package org.virtuslab.unicorn

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import org.virtuslab.unicorn.TestUnicorn.profile.api._
import slick.dbio.{ DBIOAction }

import scala.concurrent.Await
import scala.concurrent.duration._

trait LongTestUnicorn {
  lazy val unicorn = TestUnicorn
}

trait BaseTest[Underlying] extends FlatSpecLike with Matchers with BeforeAndAfterEach with ScalaFutures {

  val unicorn: Unicorn[Underlying] with HasJdbcProfile

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  lazy val DB = unicorn.profile.backend.Database.forURL(dbURL, driver = dbDriver)

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  case class IntentionalRollbackException() extends Exception("Transaction intentionally aborted")

  def runWithRollback[R, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](action: DBIOAction[R, S, E]): Unit = {
    try {
      val block = action andThen DBIO.failed(new IntentionalRollbackException())
      val future = DB.run(block.transactionally)
      Await.result(future, 5.seconds)
    } catch {
      case e: IntentionalRollbackException => // Success
    }
  }

}