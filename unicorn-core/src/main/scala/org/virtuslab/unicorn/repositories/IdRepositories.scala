package org.virtuslab.unicorn.repositories

import java.sql.SQLException

import org.virtuslab.unicorn.{ HasJdbcDriver, Identifiers, Tables }
import scala.concurrent.ExecutionContext

protected[unicorn] trait IdRepositories[Underlying] {
  self: HasJdbcDriver with Identifiers[Underlying] with Tables[Underlying] with Repositories[Underlying] =>

  import driver.api.{ Table => _, _ }

  /**
   * Base class for all queries with an [[org.virtuslab.unicorn.Identifiers.BaseId]].
   *
   * @tparam Id type of id
   * @tparam Entity type of elements that are queried
   * @tparam Table type of table
   */
  protected trait BaseIdQueries[Id <: BaseId, Entity <: WithId[Id], Table <: IdTable[Id, Entity]] {

    /** @return query to operate on */
    protected def query: TableQuery[Table]

    /** @return type mapper for I, required for querying */
    protected implicit def mapping: BaseColumnType[Id]

    val byIdQuery = Compiled(byIdFunc _)

    /** Query all ids. */
    protected lazy val allIdsQuery = query.map(_.id)

    /** Query element by id, method version. */
    protected def byIdFunc(id: Rep[Id]) = query.filter(_.id === id)

    /** Query by multiple ids. */
    protected def byIdsQuery(ids: Seq[Id]) = query.filter(_.id inSet ids)
  }

  /**
   * Base trait for repositories where we use [[org.virtuslab.unicorn.Identifiers.BaseId]]s.
   *
   * @tparam Id type of id
   * @tparam Entity type of entity
   * @tparam Table type of table
   */
  // format: OFF
  class BaseIdRepository[Id <: BaseId, Entity <: WithId[Id], Table <: IdTable[Id, Entity]](protected val query: TableQuery[Table])
                                                                                          (implicit val mapping: BaseColumnType[Id])
      extends CommonRepositoryMethods[Entity, Table](query)
      with BaseIdQueries[Id, Entity, Table] {
    // format: ON

    protected def queryReturningId = query returning query.map(_.id)

    final val tableName = query.baseTableRow.tableName

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def findById(id: Id)(implicit session: Session): Option[Entity] = invokeAction(byIdQuery(id).result.headOption)

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @return Option(element)
     */
    def findByIdAction(id: Id): DBIO[Option[Entity]] = byIdQuery(id).result.headOption

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @param session implicit session
     * @return Option(id) of new element
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def copyAndSave(id: Id)(implicit session: Session): Option[Id] =
      findById(id).map(elem => invokeAction(queryReturningId += elem))

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @return Option(id) of new element
     */
    def copyAndSaveAction(id: Id)(implicit context: ExecutionContext): DBIO[Option[Id]] =
      findByIdAction(id).flatMap {
        elemOpt =>
          val optionalAction = elemOpt.map(elem => queryReturningId += elem)
          optionalAction.map(el => el.map(a => Option(a))).getOrElse(DBIO.successful(Option.empty[Id]))
      }

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def findExistingById(id: Id)(implicit session: Session): Entity =
      findById(id).getOrElse(throw new NoSuchFieldException(s"For id: $id in table: $tableName"))

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @return Option(element)
     */
    def findExistingByIdAction(id: Id)(implicit context: ExecutionContext): DBIO[Entity] =
      findByIdAction(id).map(_.getOrElse(throw new NoSuchFieldException(s"For id: $id in table: $tableName")))

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @param session implicit session
     * @return Seq(element)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def findByIds(ids: Seq[Id])(implicit session: Session): Seq[Entity] = invokeAction(byIdsQuery(ids).result)

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @return Seq(element)
     */
    def findByIdsAction(ids: Seq[Id]): DBIO[Seq[Entity]] = byIdsQuery(ids).result

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def deleteById(id: Id)(implicit session: Session): Int = invokeAction(byIdQuery(id).delete)

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @return number of deleted elements (0 or 1)
     */
    def deleteByIdAction(id: Id): DBIO[Int] = byIdQuery(id).delete

    /**
     * @param session implicit session
     * @return Sequence of ids
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def allIds()(implicit session: Session): Seq[Id] = invokeAction(allIdsQuery.result)

    /**
     * @return Sequence of ids
     */
    def allIdsAction(): DBIO[Seq[Id]] = allIdsQuery.result

    /**
     * Saves one element.
     *
     * @param elem element to save
     * @param session implicit session
     * @return Option(elementId)
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def save(elem: Entity)(implicit session: Session): Id = {
      elem.id match {
        case Some(id) =>
          val rowsUpdated = invokeAction(byIdFunc(id).update(elem))
          afterSave(elem)
          if (rowsUpdated == 1) id
          else throw new SQLException(s"Error during save in table: $tableName, " +
            s"for id: $id - $rowsUpdated rows updated, expected: 1. Entity: $elem")
        case None =>
          val result = invokeAction(queryReturningId += elem)
          afterSave(elem)
          result
      }
    }

    /**
     * Saves one element.
     *
     * @param elem element to save
     * @return Option(elementId)
     */
    def saveAction(elem: Entity)(implicit context: ExecutionContext): DBIO[Id] = {
      elem.id match {
        case Some(id) =>
          val rowsUpdatedAction = byIdFunc(id).update(elem)
          rowsUpdatedAction.map { rowsUpdated =>
            afterActionSave(elem)
            if (rowsUpdated == 1) id
            else throw new SQLException(s"Error during save in table: $tableName, " +
              s"for id: $id - $rowsUpdated rows updated, expected: 1. Entity: $elem")
          }
        case None =>
          val result = queryReturningId += elem
          result.map {
            id =>
              afterActionSave(elem)
              id
          }
      }
    }

    /**
     * Hook executed after element is saved - if you want to do some stuff then, override it.
     *
     * @param elem element to save
     * @param session implicit session
     */
    @deprecated("Use afterActionSave.", "0.7.2")
    protected def afterSave(elem: Entity)(implicit session: Session): Unit = {}

    /**
     * Hook executed after element is saved - if you want to do some stuff then, override it.
     *
     * @param elem element to save
     */
    protected def afterActionSave(elem: Entity)(implicit context: ExecutionContext): Unit = {}

    /**
     * Saves multiple elements.
     *
     * @param elems elements to save
     * @param session implicit database session
     * @return Sequence of ids
     */
    @deprecated("Use methods with DBIO.", "0.7.2")
    def saveAll(elems: Seq[Entity])(implicit session: Session): Seq[Id] = session.withTransaction {
      // conversion is required to force lazy collections
      elems.toIndexedSeq map save
    }

    /**
     * Saves multiple elements.
     *
     * @param elems elements to save
     * @return Sequence of ids
     */
    def saveAllAction(elems: Seq[Entity])(implicit context: ExecutionContext): DBIO[Seq[Id]] = {
      DBIO.sequence(elems.toIndexedSeq map saveAction).transactionally
    }
  }
}
