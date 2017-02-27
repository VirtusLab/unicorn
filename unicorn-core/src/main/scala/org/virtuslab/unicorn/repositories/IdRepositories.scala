package org.virtuslab.unicorn.repositories

import java.sql.SQLException

import org.virtuslab.unicorn._

import scala.concurrent.ExecutionContext

protected[unicorn] trait IdRepositories[Underlying] {
  self: HasJdbcProfile with Tables[Underlying] with Repositories[Underlying] =>

  import profile.api.{ Table => _, _ }
  /**
   * Base class for all queries with an [[org.virtuslab.unicorn.BaseId]].
   *
   * @tparam Id type of id
   * @tparam Entity type of elements that are queried
   * @tparam Table type of table
   */
  protected trait BaseIdQueries[Id <: BaseId[Underlying], Entity <: WithId[Underlying, Id], Table <: IdTable[Id, Entity]] {

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
   * Base trait for repositories where we use [[org.virtuslab.unicorn.BaseId]]s.
   *
   * @tparam Id type of id
   * @tparam Entity type of entity
   * @tparam Table type of table
   */
  // format: OFF
  class BaseIdRepository[Id <: BaseId[Underlying], Entity <: WithId[Underlying, Id], Table <: IdTable[Id, Entity]](protected val query: TableQuery[Table])
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
     * @return Option(element)
     */
    def findById(id: Id): DBIO[Option[Entity]] = byIdQuery(id).result.headOption

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @return Option(id) of new element
     */
    def copyAndSave(id: Id)(implicit ec: ExecutionContext): DBIO[Id] =
      for {
        elem <- findById(id)
        result <- elem match {
          case None => DBIO.failed(new NoSuchElementException(s"Element with $id doesn't exist"))
          case Some(elem) => (queryReturningId += (elem))
        }
      } yield result

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @return element
     */
    def findExistingById(id: Id)(implicit ec: ExecutionContext): DBIO[Entity] =
      findById(id).map {
        case Some(elem) => elem
        case None => throw new NoSuchFieldException(s"Element with id: $id in table: $tableName does not exist")
      }

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @return Seq(element)
     */
    def findByIds(ids: Seq[Id]): DBIO[Seq[Entity]] = byIdsQuery(ids).result

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @return number of deleted elements (0 or 1)
     */
    def deleteById(id: Id): DBIO[Int] = byIdQuery(id).delete

    /**
     * @return Sequence of ids
     */
    def allIds(): DBIO[Seq[Id]] = allIdsQuery.result

    /**
     * Saves one element.
     *
     * @param elem element to save
     * @return Option(elementId)
     */
    def save(elem: Entity)(implicit ec: ExecutionContext): DBIO[Id] = {
      elem.id match {
        case Some(id) =>
          val updateAction = byIdFunc(id).update(elem)
          updateAction.map { rowsUpdated =>
            afterSave(elem)
            if (rowsUpdated == 1) id
            else throw new SQLException(s"Error during save in table: $tableName, " +
              s"for id: $id - $rowsUpdated rows updated, expected: 1. Entity: $elem")
          }
        case None =>
          val result = queryReturningId += (elem)
          result.map { rowsInserted =>
            afterSave(elem)
            rowsInserted
          }
      }
    }

    /**
     * Hook executed after element is saved - if you want to do some stuff then, override it.
     *
     * @param elem element to save
     */
    protected def afterSave(elem: Entity): Unit = {}

    /**
     * Saves multiple elements.
     *
     * @param elems elements to save
     * @return Sequence of ids
     */
    def saveAll(elems: Seq[Entity])(implicit ec: ExecutionContext): DBIO[Seq[Id]] = {
      // conversion is required to force lazy collections
      val actions = elems.toIndexedSeq map save

      DBIO.sequence(actions)
    }

  }

}
