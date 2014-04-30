package org.virtuslab.unicorn.ids.repositories

import java.sql.SQLException
import org.virtuslab.unicorn.ids._
import scala.Some
import scala.slick.driver.JdbcDriver

trait IdRepositories extends Identifiers with Tables with Queries {
  self: JdbcDriver =>

  import simple._

  /**
   * Base trait for repositories where we use [[org.virtuslab.unicorn.ids.Identifiers.BaseId]]s.
   *
   * @tparam I type of id
   * @tparam A type of entity
   * @author Jerzy Müller
   */
  class BaseIdRepository[I <: BaseId, A <: WithId[I], T <: IdTable[I, A]](tableName: String, val query: TableQuery[T])
                                                                         (implicit val mapping: BaseColumnType[I])
    extends BaseIdQueries[I, A, T] {

    protected def queryReturningId = query returning query.map(_.id)

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
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    def findById(id: I)(implicit session: Session): Option[A] = byIdQuery(id).firstOption

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @param session implicit session
     * @return Option(id) of new element
     */
    def copyAndSave(id: I)(implicit session: Session): Option[I] = findById(id).map(elem => queryReturningId insert elem)

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    def findExistingById(id: I)(implicit session: Session): A =
      findById(id).getOrElse(throw new NoSuchFieldException(s"For id: $id in table: $tableName"))

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @param session implicit session
     * @return Seq(element)
     */
    def findByIds(ids: Seq[I])(implicit session: Session): Seq[A] = byIdsQuery(ids).list

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def deleteById(id: I)(implicit session: Session): Int = byIdQuery(id).delete
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
      elem.id match {
        case Some(id) =>
          val rowsUpdated = byIdFunc(id).update(elem)
          if (rowsUpdated == 1) id
          else throw new SQLException(s"Error during save in table: $tableName, " +
            s"for id: $id - $rowsUpdated rows updated, expected: 1. Entity: $elem")
        case None =>
          queryReturningId insert elem
      }
    }

    /**
     * Saves multiple elements.
     *
     * @param elems elements to save
     * @param session implicit database session
     * @return Sequence of ids
     */
    def saveAll(elems: Seq[A])(implicit session: Session): Seq[I] = session.withTransaction {
      // conversion is required to force lazy collections
      elems.toIndexedSeq map save
    }

  }
}
