package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.TestUnicorn._
import org.virtuslab.unicorn.TestUnicorn.profile.api._
import org.virtuslab.unicorn.{ BaseTest, LongTestUnicorn }
import slick.dbio.Effect.Read

import scala.concurrent.ExecutionContext.Implicits.global

class DictionaryRepositoryTest extends BaseTest[Long] with LongTestUnicorn {

  type DictionaryEntry = (String, String)

  class Dictionary(tag: Tag) extends BaseTable[DictionaryEntry](tag, "DICTIONARY") {

    def key = column[String]("key")

    def value = column[String]("value")

    def dictionaryIndex = index("dictionary_idx", (key, value), unique = true)

    def * = (key, value)
  }

  val dictQuery: TableQuery[Dictionary] = TableQuery[Dictionary]

  object DictionaryRepository extends BaseRepository[DictionaryEntry, Dictionary](dictQuery) {

    protected def findQuery(entry: DictionaryEntry) = for {
      dictionaryEntry <- query if dictionaryEntry.key === entry._1 && dictionaryEntry.value === entry._2
    } yield dictionaryEntry.value

    override protected def exists(entry: DictionaryEntry): DBIOAction[Boolean, NoStream, Read] =
      findQuery(entry).result.headOption.map(_.nonEmpty)
  }

  "Dictionary repository" should "save and query users" in runWithRollback {
    val entry = ("key", "value")

    val actions = for {
      _ <- dictQuery.schema.create
      _ <- DictionaryRepository save entry
      find1 <- DictionaryRepository.findAll()

      // when saving second time
      _ <- DictionaryRepository save entry

      // then no new entry should be added
      find2 <- DictionaryRepository.findAll()

      _ <- DictionaryRepository deleteAll ()
      find3 <- DictionaryRepository.findAll()
    } yield (find1, find2, find3)

    actions map {
      case (find1, find2, find3) =>
        find1 shouldEqual Seq(entry)
        find2 shouldEqual Seq(entry)
        find3 shouldBe empty
    }
  }

}
