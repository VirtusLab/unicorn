package org.virtuslab.unicorn.ids.repositories

import org.virtuslab.unicorn.ids.BaseId
import org.virtuslab.unicorn.ids.IdTable
import org.virtuslab.unicorn.ids.WithId
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Shape._

/**
 * Base class for all queries with an [[org.virtuslab.unicorn.ids.BaseId]].
 *
 * @tparam I type of id
 * @tparam A type of element that is queried
 * @author Jerzy Müller
 */
private[repositories] trait BaseIdQueries[I <: BaseId, A <: WithId[I], T <: IdTable[I, A]] {

  /** @return query to operate on */
  protected def query: TableQuery[T]

  /** @return type mapper for I, required for querying */
  protected implicit def mapping: BaseColumnType[I]

  val byIdQuery = Compiled(byIdFunc _)

  /** Query all ids. */
  protected lazy val allIdsQuery = query.map(_.id)

  /** Query element by id, method version. */
  protected def byIdFunc(id: Column[I]) = query.filter(_.id === id)

  /** Query by multiple ids. */
  protected def byIdsQuery(ids: Seq[I]) = query.filter(_.id inSet ids)
}