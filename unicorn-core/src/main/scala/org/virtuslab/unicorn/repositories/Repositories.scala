package org.virtuslab.unicorn.repositories

import org.virtuslab.unicorn.{ HasJdbcProfile, Tables }

import scala.concurrent.ExecutionContext

protected[unicorn] trait Repositories[Underlying]
  extends JunctionRepositories[Underlying]
  with IdRepositories[Underlying] {
  self: HasJdbcProfile with Tables[Underlying] =>

  import profile.api._

  /**
   * Implementation detail - common methods for all repositories.
   */
  private[repositories] abstract class CommonRepositoryMethods[Entity, T <: Table[Entity]](query: TableQuery[T]) {

    /**
     * @return all elements of type A
     */
    def findAll(): DBIO[Seq[Entity]] = query.result

    /**
     * Deletes all elements in table.
     * @return number of deleted elements
     */
    def deleteAll(): DBIO[Int] = query.delete

    /**
     * Creates table definition in database.
     *
     */
    def create(): DBIO[Unit] = query.schema.create

    /**
     * Drops table definition from database.
     *
     */
    def drop(): DBIO[Unit] = query.schema.drop
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
     * @return elem itself
     */
    def save(elem: Entity)(implicit ec: ExecutionContext): DBIO[Entity] = exists(elem).flatMap {
      case true => DBIO.successful(elem)
      case false => {
        val insert = query += (elem)
        val result = insert.map(_ => elem)
        result
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
    protected def exists(elem: Entity): DBIO[Boolean]

  }

}
