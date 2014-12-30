package org.virtuslab.unicorn.repositories

import java.sql.SQLException

import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }

import scala.concurrent.Future

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

    private implicit val executionContext = db.executor.executionContext

    /**
     * Deletes one element.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def delete(first: First, second: Second)(implicit session: Session): Future[Int] =
      db.run(findOneQueryCompiled((first, second)).delete)

    /**
     * Checks if element exists in database.
     *
     * @param first element of junction
     * @param second element of junction
     * @param session implicit database session
     * @return true if element exists in database
     */
    def exists(first: First, second: Second)(implicit session: Session): Future[Boolean] =
      db.run(existsQuery((first, second)).result)

    /**
     * Saves one element if it's not present in db already.
     *
     * @param a one element
     * @param b other element
     * @param session implicit session
     */
    def save(a: First, b: Second)(implicit session: Session): Future[_] = {
      exists(a, b).map { existing =>
        if (existing) {
          db.run(query += ((a, b)))
        } else {
          throw new SQLException(s"Could not insert ${(a, b).toString} to table")
        }
      }
    }

    /**
     * @param a element to query by
     * @return all b values for given a
     */
    def forA(a: First)(implicit session: Session): Future[Seq[Second]] = db.run(findSecondByFirstQuery(a).result)

    /**
     * @param b element to query by
     * @return all a values for given b
     */
    def forB(b: Second)(implicit session: Session): Future[Seq[First]] = db.run(findFirstBySecondQuery(b).result)

    /**
     * Delete all rows with given a value.
     * @param a element to query by
     */
    def deleteForA(a: First)(implicit session: Session): Future[Int] = db.run(findByFirstQueryCompiled(a).delete)

    /**
     * Delete all rows with given b value.
     * @param b element to query by
     */
    def deleteForB(b: Second)(implicit session: Session): Future[Int] = db.run(findBySecondQuery(b).delete)

  }

}
