package org.virtuslab.unicorn.dsl

import org.virtuslab.unicorn.{ HasJdbcDriver, Unicorn }

import scala.slick.lifted.TableQuery

/**
 * TODO
 *
 * @param unicorn
 * @tparam Underlying type of id
 */
abstract class EntityDsl[Underlying](val unicorn: Unicorn[Underlying] with HasJdbcDriver) {

  import unicorn._

  /** TODO */
  case class Id(id: Underlying) extends BaseId

  /** TODO */
  object Id extends CoreCompanion[Id]

  /** TODO */
  type Row <: BaseRow

  /** TODO */
  final type BaseRow = WithId[Id]

  /** TODO */
  type Table <: BaseTable

  /** TODO */
  final type BaseTable = IdTable[Id, Row]

  /** TODO */
  def query: TableQuery[Table]

  /**
   * TODO
   * @param mapping
   */
  class BaseRepository(implicit mapping: driver.simple.BaseColumnType[Id])
    extends BaseIdRepository[Id, Row, Table](query)(mapping)
}
