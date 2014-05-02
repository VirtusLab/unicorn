package org.virtuslab.unicorn.ids.repositories

import org.virtuslab.unicorn.ids.{ HasJdbcDriver, Tables, Identifiers }
import scala.slick.driver.JdbcDriver
import scala.slick.lifted.Shape._

protected[unicorn] trait JunctionRepositories {
  self: HasJdbcDriver with Tables with Identifiers =>

  import driver.simple._

  /**
   * Base queries for junction tables
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  private[repositories] trait JunctionQueries[First, Second] {

    protected def table: JunctionTable[First, Second]

    protected def query: TableQuery[JunctionTable[First, Second]]

    private implicit def aImpl = table.aMapper

    private implicit def bImpl = table.bMapper

    protected val getQuery = for {
      (a, b) <- Parameters[(First, Second)]
      en <- query if en.columns._1 === a && en.columns._2 === b
    } yield en

    protected val getByAQuery = for {
      a <- Parameters[First]
      en <- query if en.columns._1 === a
    } yield en.columns._2

    protected val getByBQuery = for {
      b <- Parameters[Second]
      en <- query if en.columns._2 === b
    } yield en.columns._1

    protected def getByAQueryFunc(a: First) = for {
      en <- query if en.columns._1 === a
    } yield en

    protected def getByBQueryFunc(b: Second) = for {
      en <- query if en.columns._2 === b
    } yield en

    protected def getQueryFunc(a: First, b: Second) = for {
      en <- query if en.columns._1 === a && en.columns._2 === b
    } yield en

  }

  /**
   * Repository with basic methods for junction tables.
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  trait JunctionRepository[First, Second] extends JunctionQueries[First, Second] {

    /**
     * Deletes one element.
     *
     * @param elem element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def delete(elem: (First, Second))(implicit session: Session): Int =
      getQueryFunc(elem._1, elem._2).delete

    /**
     * Checks if element exists in database.
     *
     * @param elem element to check for
     * @param session implicit database session
     * @return true if element exists in database
     */
    def exists(elem: (First, Second))(implicit session: Session): Boolean =
      getQueryFunc(elem._1, elem._2).firstOption.isDefined

    /**
     * @param session implicit session param for query
     * @return all elements of type (First, Second)
     */
    def findAll()(implicit session: Session): Seq[(First, Second)] = Query(table).list

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     * @param session implicit session
     * @return Option(elementId)
     */
    def save(a: First, b: Second)(implicit session: Session): Unit = {
      if (getQuery((a, b)).firstOption.isEmpty) {
        table.insert((a, b))
      }
    }

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    def forA(a: First)(implicit session: Session): Seq[Second] = getByAQuery(a).list

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    def forB(b: Second)(implicit session: Session): Seq[First] = getByBQuery(b).list

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    def deleteForA(a: First)(implicit session: Session): Int = {
      getByAQueryFunc(a).delete
    }

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    def deleteForB(b: Second)(implicit session: Session): Int = {
      getByBQueryFunc(b).delete
    }

  }

}