package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ LongTestUnicorn, TestUnicorn, BaseTest }
import TestUnicorn._
import TestUnicorn.driver.api._

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class DictionaryRepositoryTest extends BaseTest[Long] with LongTestUnicorn {

  behavior of "DictionaryRepository"

  it should "save and query users" in rollback {
    implicit session =>
      Await.result(invokeActionAsync(dictQuery.schema.create), 10.seconds)

      // when
      val entry = ("key", "value")
      DictionaryRepository save entry

      // then
      DictionaryRepository.findAll() shouldEqual Seq(entry)

      // when saving second time
      DictionaryRepository save entry

      // then no new entry should be added
      DictionaryRepository.findAll() shouldEqual Seq(entry)

      // when
      DictionaryRepository.deleteAll()

      // then
      DictionaryRepository.findAll() shouldBe empty
  }

  it should "save and query users using DBIO" in rollbackAction {
    val entry = ("key", "value")
    for {
      _ <- dictQuery.schema.create
      _ <- DictionaryActionRepository.saveAction(entry)
      all <- DictionaryActionRepository.findAllAction()
    } yield {
      all shouldEqual Seq(entry)
    }
  }

  it should "save only once using DBIO" in rollbackAction {
    val entry = ("key", "value")
    for {
      _ <- dictQuery.schema.create
      _ <- DictionaryActionRepository.saveAction(entry)
      _ <- DictionaryActionRepository.saveAction(entry)
      all <- DictionaryRepository.findAllAction()
    } yield {
      all shouldEqual Seq(entry)
    }
  }

  it should "delete elements using DBIO" in rollbackAction {
    val entry = ("key", "value")
    for {
      _ <- dictQuery.schema.create
      _ <- DictionaryActionRepository.saveAction(entry)
      _ <- DictionaryActionRepository.deleteAllAction()
      all <- DictionaryRepository.findAllAction()
    } yield {
      all shouldBe empty
    }
  }

  type DictionaryEntry = (String, String)

  class Dictionary(tag: Tag) extends BaseTable[DictionaryEntry](tag, "DICTIONARY") {

    def key = column[String]("key")

    def value = column[String]("value")

    def dictionaryIndex = index("dictionary_idx", (key, value), unique = true)

    def * = (key, value)
  }

  val dictQuery: TableQuery[Dictionary] = TableQuery[Dictionary]

  object DictionaryActionRepository extends BaseActionRepository[DictionaryEntry, Dictionary](dictQuery) {

    protected def findQuery(entry: DictionaryEntry) = for {
      dictionaryEntry <- query if dictionaryEntry.key === entry._1 && dictionaryEntry.value === entry._2
    } yield dictionaryEntry.value

    override protected def exists(entry: DictionaryEntry)(implicit executionContext: ExecutionContext): DBIO[Boolean] =
      findQuery(entry).result.headOption.map(_.nonEmpty)
  }

  object DictionaryRepository extends BaseRepository[DictionaryEntry, Dictionary](dictQuery) {

    protected def findQuery(entry: DictionaryEntry) = for {
      dictionaryEntry <- query if dictionaryEntry.key === entry._1 && dictionaryEntry.value === entry._2
    } yield dictionaryEntry.value

    override protected def exists(entry: DictionaryEntry)(implicit session: Session): Boolean =
      invokeAction(findQuery(entry).result.headOption).nonEmpty
  }
}
