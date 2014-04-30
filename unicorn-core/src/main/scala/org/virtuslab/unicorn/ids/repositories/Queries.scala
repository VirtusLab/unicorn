package org.virtuslab.unicorn.ids.repositories

import org.virtuslab.unicorn.ids._
import scala.slick.driver.JdbcDriver
import scala.slick.lifted.Shape._

trait Queries extends Identifiers with Tables {
  self: JdbcDriver =>

  import profile.simple._

  /**
   * Base class for all queries with an [[org.virtuslab.unicorn.ids.Identifiers.BaseId]].
   *
   * @tparam Id type of id
   * @tparam Entity type of elements that are queried
   * @tparam Table type of table
   * @author Jerzy Müller
   */
  private[repositories] trait BaseIdQueries[Id <: BaseId, Entity <: WithId[Id], Table <: IdTable[Id, Entity]] {

    /** @return query to operate on */
    protected def query: TableQuery[Table]

    /** @return type mapper for I, required for querying */
    protected implicit def mapping: BaseColumnType[Id]

    val byIdQuery = Compiled(byIdFunc _)

    /** Query all ids. */
    protected lazy val allIdsQuery = query.map(_.id)

    /** Query element by id, method version. */
    protected def byIdFunc(id: Column[Id]) = query.filter(_.id === id)

    /** Query by multiple ids. */
    protected def byIdsQuery(ids: Seq[Id]) = query.filter(_.id inSet ids)
  }

}