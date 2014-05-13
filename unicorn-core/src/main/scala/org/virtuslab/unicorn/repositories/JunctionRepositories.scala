package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }

protected[unicorn] trait JunctionRepositories {
  self: HasJdbcDriver with Tables with Identifiers =>

  import driver.simple._

  /**
   * Repository with basic methods for junction tables.
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  class JunctionRepository[First: BaseColumnType, Second: BaseColumnType, Table <: JunctionTable[First, Second]](val query: TableQuery[Table]) {

    protected def findOneQueryFun(first: Column[First], second: Column[Second]) =
      query.filter(row => row.columns._1 === first && row.columns._2 === second)

    protected val findOneQueryCompiled = Compiled(findOneQueryFun _)

    protected def existsQueryFun(first: Column[First], second: Column[Second]) = findOneQueryFun(first, second).exists

    protected val existsQuery = Compiled(existsQueryFun _)

    protected def findByFirstFun(first: Column[First]) = query.filter(_.columns._1 === first)

    protected val findByFirstQueryCompiled = Compiled(findByFirstFun _)

    protected def findSecondByFirstFun(first: Column[First]) = findByFirstFun(first).map(_.columns._2)

    protected val findSecondByFirstQuery = Compiled(findSecondByFirstFun _)

    protected def findBySecondFun(second: Column[Second]) = query.filter(_.columns._2 === second)

    protected val findBySecondQuery = Compiled(findBySecondFun _)

    protected def findFirstBySecondFun(second: Column[Second]) = findBySecondFun(second).map(_.columns._1)

    protected val findFirstBySecondQuery = Compiled(findFirstBySecondFun _)

    /**
     * Deletes one element.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def delete(first: First, second: Second)(implicit session: Session): Int =
      findOneQueryCompiled((first, second)).delete

    /**
     * Checks if element exists in database.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit database session
     * @return true if element exists in database
     */
    def exists(first: First, second: Second)(implicit session: Session): Boolean =
      existsQuery((first, second)).run

    /**
     * @param session implicit session param for query
     * @return all elements of type (First, Second)
     */
    def findAll()(implicit session: Session): Seq[(First, Second)] = query.run

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     * @param session implicit session
     * @return Option(elementId)
     */
    def save(a: First, b: Second)(implicit session: Session): Unit = query.insert((a, b))

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    def forA(a: First)(implicit session: Session): Seq[Second] = findSecondByFirstQuery(a).run

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    def forB(b: Second)(implicit session: Session): Seq[First] = findFirstBySecondQuery(b).run

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    def deleteForA(a: First)(implicit session: Session): Int = findByFirstQueryCompiled(a).delete

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    def deleteForB(b: Second)(implicit session: Session): Int = findBySecondQuery(b).delete

  }

}