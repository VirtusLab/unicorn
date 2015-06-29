package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ HasJdbcDriver, Tables, Identifiers }

protected[unicorn] trait Repositories[Underlying]
    extends JunctionRepositories[Underlying]
    with IdRepositories[Underlying] {
  self: HasJdbcDriver with Identifiers[Underlying] with Tables[Underlying] =>

  import driver.simple._

  /**
   * Implementation detail - common methods for all repositories.
   */
  private[repositories] abstract class CommonRepositoryMethods[Entity, T <: Table[Entity]](query: TableQuery[T]) {

    /**
     * @param session implicit session param for query
     * @return all elements of type A
     */
    def findAll()(implicit session: Session): Seq[Entity] = query.list

    /**
     * Deletes all elements in table.
     * @param session implicit session param for query
     * @return number of deleted elements
     */
    def deleteAll()(implicit session: Session): Int = query.delete

    /**
     * Creates table definition in database.
     *
     * @param session implicit database session
     */
    def create()(implicit session: Session): Unit =
      query.ddl.create

    /**
     * Drops table definition from database.
     *
     * @param session implicit database session
     */
    def drop()(implicit session: Session): Unit =
      query.ddl.drop
  }

  /**
   * Base for services for entities that have no type-safe id created - for example join tables.
   *
   * @tparam Entity type of entity
   * @tparam T type of table
   * @param query base table query
   */
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
        query.insert(elem)
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

}
