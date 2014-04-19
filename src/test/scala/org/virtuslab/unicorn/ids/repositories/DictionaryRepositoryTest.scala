package org.virtuslab.unicorn.ids.repositories

import org.virtuslab.unicorn.ids.{BaseTable, AppTest}
import play.api.db.slick.Config.driver.simple._

class DictionaryRepositoryTest extends AppTest {

  type DictionaryEntry = (String, String)

  class Dictionary(tag: Tag) extends BaseTable[DictionaryEntry](tag, "DICTIONARY") {

    def key = column[String]("key", O.NotNull)

    def value = column[String]("value", O.NotNull)

    def dictionaryIndex = index("dictionary_idx", (key, value), unique = true)

    def * = (key, value)
  }

  "Dictionary repository" should "save and query users" in rollback {
    implicit session =>
    // setup
      val dictQuery: TableQuery[Dictionary] = TableQuery[Dictionary]
      object DictionaryRepository extends BaseRepository[DictionaryEntry, Dictionary](dictQuery) {

        protected def findQuery(entry: DictionaryEntry) = for {
          dictionaryEntry <- query if dictionaryEntry.key === entry._1 && dictionaryEntry.value === entry._2
        } yield dictionaryEntry.value

        override protected def exists(entry: DictionaryEntry)(implicit session: Session): Boolean =
          findQuery(entry).firstOption.nonEmpty
      }
      dictQuery.ddl.create

      // when
      val entry = ("key", "value")
      DictionaryRepository save entry

      // then
      DictionaryRepository.findAll() should contain(entry)

      // when
      DictionaryRepository.deleteAll()

      // then
      DictionaryRepository.findAll() shouldBe 'empty
  }

}
