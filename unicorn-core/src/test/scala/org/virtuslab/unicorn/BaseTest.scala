package org.virtuslab.unicorn

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.{ Failure, Try }

trait RollbackHelper[Underlying] {

  val unicorn: Unicorn[Underlying] with HasJdbcDriver
  implicit val timeout = Timeout(10.seconds)

  import unicorn.driver.api._

  def DB: unicorn.driver.profile.backend.DatabaseDef

  object RollbackException extends Exception

  /**
   * Runs function in rolled-back transaction.
   *
   * @param func function to run in rolled-back transaction
   * @tparam Result type returned by `f`
   * @return value returned from `f`
   */
  def rollback[Result](func: Session => Result): Result = DB.withTransaction {
    session: Session =>
      val out = func(session)
      session.rollback()
      out
  }

  /**
   * Runs action in rolled-back transaction.
   *
   * @param func function to run in rolled-back transaction
   * @tparam Result type returned by `f`
   * @return value returned from `f`
   */
  def rollbackAction[Result](func: => DBIO[Result]): Unit = {
    val out = func.flatMap(_ => DBIO.failed(RollbackException)).transactionally

    Try(Await.result(DB.run(out), 10.seconds)) match {
      case Failure(RollbackException) =>
      case Failure(other) => throw other
    }
  }
}

trait LongTestUnicorn {
  lazy val unicorn = TestUnicorn
}

trait BaseTest[Underlying] extends FlatSpecLike with Matchers with BeforeAndAfterEach with RollbackHelper[Underlying] {

  val dbURL = "jdbc:h2:mem:unicorn"

  val dbDriver = "org.h2.Driver"

  override lazy val DB = unicorn.driver.profile.backend.Database.forURL(dbURL, driver = dbDriver)
  //    override lazy val DB = Database.forURL(dbURL, driver = dbDriver).asInstanceOf[unicorn.driver.profile.backend.DatabaseDef]
}