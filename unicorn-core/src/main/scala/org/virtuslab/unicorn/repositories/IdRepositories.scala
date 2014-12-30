package org.virtuslab.unicorn.repositories

import java.sql.{ SQLDataException, SQLException }
import java.util.NoSuchElementException

import org.virtuslab.unicorn.{ HasJdbcDriver, Identifiers, Tables }

import scala.concurrent.Future

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

    private implicit val executionContext = db.executor.executionContext

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    def findById(id: Id)(implicit session: Session): Future[Option[Entity]] = db.run(byIdQuery(id).result.headOption)

    /**
     * Clones element by id.
     *
     * @param id id of element to clone
     * @param session implicit session
     * @return Option(id) of new element
     */
    def copyAndSave(id: Id)(implicit session: Session): Future[Id] =
      findById(id).flatMap {
        case Some(elem) => db.run(queryReturningId += elem)
        case None => Future.failed(new NoSuchElementException(s"Element with $id doesn't exist"))
      } // FIXME move this future to DbAction, and move execution context to unicorn

    /**
     * Finds one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return Option(element)
     */
    def findExistingById(id: Id)(implicit session: Session): Future[Entity] =
      findById(id).flatMap {
        case Some(elem) => Future.successful(elem)
        case None => Future.failed(new scala.NoSuchElementException(s"Element with $id doesn't exist"))
      }

    /**
     * Finds elements by given ids.
     *
     * @param ids ids of element
     * @param session implicit session
     * @return Seq(element)
     */
    def findByIds(ids: Seq[Id])(implicit session: Session): Future[Seq[Entity]] = db.run(byIdsQuery(ids).result)

    /**
     * Deletes one element by id.
     *
     * @param id id of element
     * @param session implicit session
     * @return number of deleted elements (0 or 1)
     */
    def deleteById(id: Id)(implicit session: Session): Future[Int] = db.run(byIdQuery(id).delete).map {
      _.ensuring(_ <= 1, "Delete by id removed more than one row")
    }(db.executor.executionContext)

    /**
     * @param session implicit session
     * @return Sequence of ids
     */
    def allIds()(implicit session: Session): Future[Seq[Id]] = db.run(allIdsQuery.result)

    /**
     * Saves one element.
     *
     * @param elem element to save
     * @param session implicit session
     * @return Option(elementId)
     */
    def save(elem: Entity)(implicit session: Session): Future[Id] = {
      db.run(queryReturningId.insertOrUpdate(elem)).flatMap {
        case Some(id) =>
          afterSave(elem)
          Future.successful(id)
        case None =>
          Future.failed(new SQLDataException(s"Exception on insertin element $elem"))
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
    def saveAll(elems: Seq[Entity])(implicit session: Session): Future[Seq[Id]] = session.withTransaction {
      // conversion is required to force lazy collections
      Future.sequence(elems.toIndexedSeq map save)
    }
  }

}
