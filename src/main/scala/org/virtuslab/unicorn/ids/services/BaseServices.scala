package org.virtuslab.unicorn.ids.services

import java.sql.SQLException
import play.api.db.slick.Config.driver.simple._
import slick.session.Session
import org.virtuslab.unicorn.ids.{ WithId, BaseId }

/**
 * Base for services for entities that have no type-safe id created - for example join tables.
 *
 * @tparam A type of entity
 * @author Jerzy Müller
 */
trait BaseService[A] {
  self: BaseQueries[A] =>

  /**
   * @param session implicit session param for query
   * @return all elements of type A
   */
  def findAll()(implicit session: Session): Seq[A] = allQuery.list()

  /**
   * Deletes all elements in table.
   * @param session implicit session param for query
   * @return number of deleted elements
   */
  def deleteAll()(implicit session: Session): Int = allQuery.delete

  /**
   * Saves one element. Warning - if element already exist, it's not updated.
   *
   * @param elem element to save
   * @param session implicit database session
   * @return elem itself
   */
  def save(elem: A)(implicit session: Session): A = {
    if (!exists(elem)) {
      table.insert(elem)
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

/**
 * Base trait for services where we use [[org.virtuslab.unicorn.ids.BaseId]]s.
 *
 * @tparam I type of id
 * @tparam A type of entity
 * @author Jerzy Müller
 */
trait BaseIdService[I <: BaseId, A <: WithId[I]] {
  self: BaseIdQueries[I, A] =>

  /**
   * @param session implicit session param for query
   * @return all elements of type A
   */
  def findAll()(implicit session: Session): Seq[A] = allQuery.list()

  /**
   * Deletes all elements in table.
   * @param session implicit session param for query
   * @return number of deleted elements
   */
  def deleteAll()(implicit session: Session): Int = allQuery.delete

  /**
   * Finds one element by id.
   *
   * @param id id of element
   * @param session implicit session
   * @return Option(element)
   */
  def findById(id: I)(implicit session: Session): Option[A] = byIdQuery(id).firstOption

  /**
   * Clone element by id.
   *
   * @param id id of element to clone
   * @param session implicit session
   * @return Option(id) of new element
   */
  def copyAndSave(id: I)(implicit session: Session): Option[I] = findById(id).map(elem => table.insertOne(elem))

  /**
    * Finds one element by id.
    *
    * @param id id of element
    * @param session implicit session
    * @return Option(element)
    */
   def findExistingById(id: I)(implicit session: Session): A =
    findById(id).getOrElse(throw new NoSuchFieldException(s"For id: $id in table: ${table.tableName}"))

  /**
   * Finds elements by given ids.
   *
   * @param ids ids of element
   * @param session implicit session
   * @return Seq(element)
   */
  def findByIds(ids: Traversable[I])(implicit session: Session): Seq[A] = byIdsQuery(ids).list

  /**
   * Deletes one element by id.
   *
   * @param id id of element
   * @param session implicit session
   * @return number of deleted elements (0 or 1)
   */
  def deleteById(id: I)(implicit session: Session): Int = byIdFunc(id).delete
    .ensuring(_ <= 1, "Delete by id removed more than one row")

  /**
   * @param session implicit session
   * @return Sequence of ids
   */
  def allIds()(implicit session: Session): Seq[I] = allIdsQuery.list()

  /**
   * Saves one element.
   *
   * @param elem element to save
   * @param session implicit session
   * @return Option(elementId)
   */
  def save(elem: A)(implicit session: Session): I = {
    elem.id.map {
      id =>
        val rowsUpdated = byIdFunc(id).update(elem)
        if (rowsUpdated == 1) id
        else throw new SQLException(s"Error during save in table: ${table.tableName}, " +
          s"for id: $id - $rowsUpdated rows updated, expected: 1. Entity: $elem")
    }.getOrElse(
      table.insertOne(elem)
    )
  }

  /**
   * Saves multiple elements.
   *
   * @param elems elements to save
   * @param session implicit database session
   * @return Sequence of ids
   */
  def saveAll(elems: Seq[A])(implicit session: Session): Seq[I] = session.withTransaction {
    elems.toIndexedSeq map save
  }

}
