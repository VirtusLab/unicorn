package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }
import scala.slick.lifted.{ TableQuery => SlickQuery }

protected[unicorn] trait JunctionRepositories {
  self: HasJdbcDriver with Tables with Identifiers =>

  import driver.simple._

  /**
   * Base queries for junction tables
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  private[repositories] trait JunctionQueries[First, Second, Table <: JunctionTable[First, Second]] {

    val query: SlickQuery[Table]

    private implicit def aImpl = ???

    private implicit def bImpl = ???

    protected val getQuery = ???

    protected val getByAQuery = ???

    protected val getByBQuery = ???

    protected def getByAQueryFunc(a: First) = ???

    protected def getByBQueryFunc(b: Second) = ???

    protected def getQueryFunc(a: First, b: Second) = ???

  }

  /**
   * Repository with basic methods for junction tables.
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  class JunctionRepository[First, Second, Table <: JunctionTable[First, Second]](val query: TableQuery[Table]) extends JunctionQueries[First, Second, Table] {

    /**
     * Deletes one element.
     *
     * @param elem element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def delete(elem: (First, Second))(implicit session: Session): Int = ???

    /**
     * Checks if element exists in database.
     *
     * @param elem element to check for
     * @param session implicit database session
     * @return true if element exists in database
     */
    def exists(elem: (First, Second))(implicit session: Session): Boolean = ???

    /**
     * @param session implicit session param for query
     * @return all elements of type (First, Second)
     */
    def findAll()(implicit session: Session): Seq[(First, Second)] = ???

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     * @param session implicit session
     * @return Option(elementId)
     */
    def save(a: First, b: Second)(implicit session: Session): Unit = ???

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    def forA(a: First)(implicit session: Session): Seq[Second] = ???

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    def forB(b: Second)(implicit session: Session): Seq[First] = ???

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    def deleteForA(a: First)(implicit session: Session): Int = ???

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    def deleteForB(b: Second)(implicit session: Session): Int = ???

  }

}