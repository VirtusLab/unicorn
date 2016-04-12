package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }

import scala.concurrent.ExecutionContext

protected[unicorn] trait JunctionRepositories[Underlying] {
  self: HasJdbcDriver with Tables[Underlying] with Identifiers[Underlying] with Repositories[Underlying] =>

  import driver.api.{ Table => _, _ }

  /**
   * Repository with basic methods for junction tables.
   * @tparam First type of one entity
   * @tparam Second type of other entity
   */
  class JunctionRepository[First: BaseColumnType, Second: BaseColumnType, Table <: JunctionTable[First, Second]](val query: TableQuery[Table])
      extends CommonRepositoryMethods[(First, Second), Table](query) {

    protected def findOneQueryFun(first: Rep[First], second: Rep[Second]) =
      query.filter(row => row.columns._1 === first && row.columns._2 === second)

    protected val findOneQueryCompiled = Compiled(findOneQueryFun _)

    protected def existsQueryFun(first: Rep[First], second: Rep[Second]) = findOneQueryFun(first, second).exists

    protected val existsQuery = Compiled(existsQueryFun _)

    protected def findByFirstFun(first: Rep[First]) = query.filter(_.columns._1 === first)

    protected val findByFirstQueryCompiled = Compiled(findByFirstFun _)

    protected def findSecondByFirstFun(first: Rep[First]) = findByFirstFun(first).map(_.columns._2)

    protected val findSecondByFirstQuery = Compiled(findSecondByFirstFun _)

    protected def findBySecondFun(second: Rep[Second]) = query.filter(_.columns._2 === second)

    protected val findBySecondQuery = Compiled(findBySecondFun _)

    protected def findFirstBySecondFun(second: Rep[Second]) = findBySecondFun(second).map(_.columns._1)

    protected val findFirstBySecondQuery = Compiled(findFirstBySecondFun _)

    /**
     * Deletes one element.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def delete(first: First, second: Second)(implicit session: Session): Int =
      invokeAction(findOneQueryCompiled((first, second)).delete)

    /**
     * Deletes one element.
     *
     * @param first element of junction
     * @param second element of junction
     * @return number of deleted elements (0 or 1)
     */
    def deleteAction(first: First, second: Second): DBIO[Int] =
      findOneQueryCompiled((first, second)).delete

    /**
     * Checks if element exists in database.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit database session
     * @return true if element exists in database
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def exists(first: First, second: Second)(implicit session: Session): Boolean =
      invokeAction(existsQuery((first, second)).result)

    /**
     * Checks if element exists in database.
     *
     * @param first element of junction
     * @param second element of junction
     * @return true if element exists in database
     */
    def existsAction(first: First, second: Second): DBIO[Boolean] =
      existsQuery((first, second)).result

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     * @param session implicit session
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def save(a: First, b: Second)(implicit session: Session): Unit = {
      if (!exists(a, b)) invokeAction(query += (a -> b))
    }

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     */
    def saveAction(a: First, b: Second)(implicit executionContext: ExecutionContext): DBIO[Unit] = {
      existsAction(a, b).flatMap { existing =>
        if (!existing) {
          val insert = query += ((a, b))
          val result = insert.map(_ => ())
          result
        } else {
          DBIO.successful(())
        }
      }
    }

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def forA(a: First)(implicit session: Session): Seq[Second] = invokeAction(findSecondByFirstQuery(a).result)

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    def forAAction(a: First): DBIO[Seq[Second]] = findSecondByFirstQuery(a).result

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def forB(b: Second)(implicit session: Session): Seq[First] = invokeAction(findFirstBySecondQuery(b).result)

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    def forBAction(b: Second): DBIO[Seq[First]] = findFirstBySecondQuery(b).result

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def deleteForA(a: First)(implicit session: Session): Int = invokeAction(findByFirstQueryCompiled(a).delete)

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    def deleteForAAction(a: First): DBIO[Int] = findByFirstQueryCompiled(a).delete

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def deleteForB(b: Second)(implicit session: Session): Int = invokeAction(findBySecondQuery(b).delete)

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    def deleteForBAction(b: Second): DBIO[Int] = findBySecondQuery(b).delete
  }

}
