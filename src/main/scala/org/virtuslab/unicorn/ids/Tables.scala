package org.virtuslab.unicorn.ids

import scala.slick.lifted.{ NumericTypeMapper, BaseTypeMapper }
import play.api.db.slick.Config.driver.simple._

/**
 * Base class for all tables that contains an id.
 *
 * @param schemaName name of schema (optional)
 * @param tableName name of the table
 * @param mapping mapping for id of this table
 * @tparam I type of id
 * @tparam A type of table
 * @author Krzysztof Romanowski, Jerzy Müller
 */
abstract class IdTable[I <: BaseId, A <: WithId[I]](schemaName: Option[String], tableName: String)
                                                   (implicit val mapping: IdTable.NTM[I])
  extends BaseTable[A](schemaName, tableName)
  with SavingMethods[I, A, IdTable[I, A]] {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tableName: String)(implicit mapping: IdTable.NTM[I]) = this(None, tableName)

  /** @return id column representation of this table */
  final def id = column[I]("id", O.PrimaryKey, O.AutoInc)

  /**
   * Method for inserting one element, to be implemented by subtypes.
   *
   * @param elem element to insert
   * @param session implicit session
   * @return
   */
  def insertOne(elem: A)(implicit session: Session): I
}

private[ids] object IdTable {
  /** Short for Numeric type mapper */
  type NTM[A] = BaseTypeMapper[A] with NumericTypeMapper
}

/**
 * Base trait for all tables. If you want to add some helpers methods for tables, here is the place.
 *
 * @param schemaName name of schema (optional)
 * @param tableName name of the table
 * @tparam A type of table
 * @author Krzysztof Romanowski, Jerzy Müller
 */
abstract class BaseTable[A](schemaName: Option[String], tableName: String)
    extends Table[A](schemaName, tableName)
    with CustomTypeMappers {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tableName: String) = this(None, tableName)
}

/**
 * Base table for simple linking between two values
 *
 * @param schemaName name of schema (optional)
 * @param tableName name of the table
 * @tparam A type of one entity
 * @tparam B type of other entity
 * @author Krzysztof Romanowski, Jerzy Müller
 */
abstract class JunctionTable[A: BaseTypeMapper, B: BaseTypeMapper](schemaName: Option[String], tableName: String)
  extends Table[(A, B)](schemaName, tableName) {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tableName: String) = this(None, tableName)

  /** Type mapper for A type */
  val aMapper = implicitly[BaseTypeMapper[A]]

  /** Type mapper for B type */
  val bMapper = implicitly[BaseTypeMapper[B]]

  /**
   * instead of def * = colA ~ colB write def columns = colA -> colB
   * @return
   */
  def columns: (Column[A], Column[B])

  def * = columns._1 ~ columns._2

  def uniqueValues = index(s"${tableName}_uniq_idx", *, unique = true)
}