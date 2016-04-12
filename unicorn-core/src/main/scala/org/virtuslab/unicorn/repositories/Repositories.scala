package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.utils.Invoker
import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }

import scala.concurrent.ExecutionContext

protected[unicorn] trait Repositories[Underlying]
    extends JunctionRepositories[Underlying]
    with IdRepositories[Underlying] with Invoker {
  self: HasJdbcDriver with Identifiers[Underlying] with Tables[Underlying] =>

  import driver.api._

  /**
   * Implementation detail - common methods for all repositories.
   */
  private[repositories] abstract class CommonRepositoryMethods[Entity, T <: Table[Entity]](query: TableQuery[T]) {

    /**
     * @param session implicit session param for query
     * @return all elements of type A
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def findAll()(implicit session: Session): Seq[Entity] = invokeAction(query.result)

    /**
     * @return all elements of type A
     */
    def findAllAction(): DBIO[Seq[Entity]] = query.result

    /**
     * Deletes all elements in table.
     * @param session implicit session param for query
     * @return number of deleted elements
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def deleteAll()(implicit session: Session): Int = invokeAction(query.delete)

    /**
     * Deletes all elements in table.
     * @return number of deleted elements
     */
    def deleteAllAction(): DBIO[Int] = query.delete

    /**
     * Creates table definition in database.
     *
     * @param session implicit database session
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def create()(implicit session: Session): Unit =
      invokeAction(query.schema.create)

    /**
     * Creates table definition in database.
     *
     */
    def createAction(): DBIO[Unit] =
      query.schema.create

    /**
     * Drops table definition from database.
     *
     * @param session implicit database session
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def drop()(implicit session: Session): Unit =
      invokeAction(query.schema.drop)

    /**
     * Drops table definition from database.
     *
     */
    def dropAction(): DBIO[Unit] =
      query.schema.drop
  }

  /**
   * Base for services for entities that have no type-safe id created - for example join tables.
   *
   * @tparam Entity type of entity
   * @tparam T type of table
   * @param query base table query
   */
  @deprecated("Use repository with DBIO.", "0.7.2")
  abstract class BaseRepository[Entity, T <: Table[Entity]](val query: TableQuery[T])
      extends CommonRepositoryMethods[Entity, T](query) {

    /**
     * Saves one element. Warning - if element already exist, it's not updated.
     *
     * @param elem element to save
     * @param session implicit database session
     * @return elem itself
     */
    def save(elem: Entity)(implicit session: Session): Entity = {
      if (!exists(elem)) {
        invokeAction(query += elem)
      }
      elem
    }

    /**
     * Checks if element exists in database. It have to be implemented by user,
     * because this is service for entities without an id and generic method
     * could not be created.
     *
     * @param elem element to check for
     * @param session implicit database session
     * @return true if element exists in database
     */
    protected def exists(elem: Entity)(implicit session: Session): Boolean

  }

  /**
   * Base for services for entities that have no type-safe id created - for example join tables.
   *
   * @tparam Entity type of entity
   * @tparam T type of table
   * @param query base table query
   */
  abstract class BaseActionRepository[Entity, T <: Table[Entity]](val query: TableQuery[T])
      extends CommonRepositoryMethods[Entity, T](query) {

    /**
     * Saves one element. Warning - if element already exist, it's not updated.
     *
     * @param elem element to save
     * @return elem itself
     */
    def saveAction(elem: Entity)(implicit executionContext: ExecutionContext): DBIO[Entity] = {
      exists(elem).flatMap { existing =>
        if (!existing) {
          (query += elem).map(_ => elem)
        } else {
          DBIO.successful(elem)
        }
      }
    }

    /**
     * Checks if element exists in database. It have to be implemented by user,
     * because this is service for entities without an id and generic method
     * could not be created.
     *
     * @param elem element to check for
     * @return true if element exists in database
     */
    protected def exists(elem: Entity)(implicit executionContext: ExecutionContext): DBIO[Boolean]

  }

}
