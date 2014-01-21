package org.virtuslab.unicorn.ids.services

// TODO - change to play-slick
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.Shape._
import org.virtuslab.unicorn.ids.{ IdTable, WithId, BaseId }

/**
 * Base class for all queries.
 *
 * @tparam A type of element that is queried
 * @author Jerzy Müller
 */
trait BaseQueries[A] {

  /** @return table to operate on */
  protected def table: Table[A]

  /** query that returns all */
  protected lazy val allQuery = Query(table)
}

/**
 * Base class for all queries with an [[org.virtuslab.unicorn.ids.BaseId]].
 *
 * @tparam I type of id
 * @tparam A type of element that is queried
 * @author Jerzy Müller
 */
trait BaseIdQueries[I <: BaseId, A <: WithId[I], T <: IdTable[I, A]] {

  /** @return table to operate on; it must be an IdTable */
  protected def table: T

  protected def query: TableQuery[T]

  /** query that returns all */
  protected lazy val allQuery = query

  /** @return type mapper for I, required for querying */
  protected implicit def mapping: BaseColumnType[I] = table.mapping

  /** Query element by id, parametrized version. */
  protected lazy val byIdQuery = for {
    id <- Parameters[I]
    o <- query if o.id === id
  } yield o

  /** Query all ids. */
  protected lazy val allIdsQuery = allQuery.map(_.id)

  /** Query element by id, method version. */
  protected def byIdFunc(id: I) = allQuery.filter(_.id === id)

  /** Query by multiple ids. */
  protected def byIdsQuery(ids: Seq[I]) = allQuery.filter(_.id inSet ids)
}