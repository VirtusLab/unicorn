package org.virtuslab.unicorn.ids.repositories

import scala.slick.driver.JdbcDriver

trait Repositories extends JunctionRepositories with IdRepositories {
  self: JdbcDriver =>

  import simple._

  /**
   * Base for services for entities that have no type-safe id created - for example join tables.
   *
   * @tparam A type of entity
   * @tparam T type of table
   * @param query base table query
   * @author Jerzy Müller
   */
  abstract class BaseRepository[A, T <: Table[A]](val query: TableQuery[T]) {

    /**
     * @param session implicit session param for query
     * @return all elements of type A
     */
    def findAll()(implicit session: Session): Seq[A] = query.list()

    /**
     * Deletes all elements in table.
     * @param session implicit session param for query
     * @return number of deleted elements
     */
    def deleteAll()(implicit session: Session): Int = query.delete

    /**
     * Saves one element. Warning - if element already exist, it's not updated.
     *
     * @param elem element to save
     * @param session implicit database session
     * @return elem itself
     */
    def save(elem: A)(implicit session: Session): A = {
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
    protected def exists(elem: A)(implicit session: Session): Boolean

  }

}