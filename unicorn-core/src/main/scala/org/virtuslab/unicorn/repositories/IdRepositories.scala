package org.virtuslab.unicorn.repositories

import java.sql.SQLException

import org.virtuslab.unicorn.{ HasJdbcDriver, Identifiers, Tables }

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
    def findById(id: Id)(implicit session: Session): Option[Entity] = invokeAction(byIdQuery(id).result.headOption)

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @param session implicit session
     * @return Option(id) of new element
     */
    def copyAndSave(id: Id)(implicit session: Session): Option[Id] =
      findById(id).map(elem => invokeAction(queryReturningId += elem))

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    def findExistingById(id: Id)(implicit session: Session): Entity =
      findById(id).getOrElse(throw new NoSuchFieldException(s"For id: $id in table: $tableName"))

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @param session implicit session
     * @return Seq(element)
     */
    def findByIds(ids: Seq[Id])(implicit session: Session): Seq[Entity] = invokeAction(byIdsQuery(ids).result)

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def deleteById(id: Id)(implicit session: Session): Int = invokeAction(byIdQuery(id).delete)

    /**
     * @param session implicit session
     * @return Sequence of ids
     */
    def allIds()(implicit session: Session): Seq[Id] = invokeAction(allIdsQuery.result)

    /**
     * Saves one element.
     *
     * @param elem element to save
     * @param session implicit session
     * @return Option(elementId)
     */
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
     * Hook executed after element is saved - if you want to do some stuff then, override it.
     *
     * @param elem element to save
     * @param session implicit session
     */
    protected def afterSave(elem: Entity)(implicit session: Session): Unit = {}

    /**
     * Saves multiple elements.
     *
     * @param elems elements to save
     * @param session implicit database session
     * @return Sequence of ids
     */
    def saveAll(elems: Seq[Entity])(implicit session: Session): Seq[Id] = session.withTransaction {
      // conversion is required to force lazy collections
      elems.toIndexedSeq map save
    }
  }

}
