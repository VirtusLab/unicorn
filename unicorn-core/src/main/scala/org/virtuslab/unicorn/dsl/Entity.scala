package org.virtuslab.unicorn.dsl

import org.virtuslab.unicorn.{HasJdbcDriver, Unicorn}

import scala.slick.lifted.TableQuery

abstract class Entity[Underlaying](val unicorn: HasJdbcDriver with Unicorn[Underlaying]) {

  import unicorn._

  case class Id(id: Underlaying) extends BaseId

  object Id extends CoreCompanion[Id]

  type Row <: BaseRow

  final type BaseRow = WithId[Id]

  type Table <: BaseTable

  final type BaseTable = IdTable[Id, Row]


  def query: TableQuery[Table]

  class BaseRepository(implicit mapping: driver.simple.BaseColumnType[Id]) 
    extends BaseIdRepository[Id, Row, Table](query)(mapping)
}

