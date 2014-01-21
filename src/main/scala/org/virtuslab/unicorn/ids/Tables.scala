package org.virtuslab.unicorn.ids

// TODO - change to play-slick
import scala.slick.driver.PostgresDriver.simple._

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
abstract class IdTable[I <: BaseId, A <: WithId[I]](tag: Tag, schemaName: Option[String], tableName: String)
                                                   (implicit val mapping: BaseColumnType[I])
  extends BaseTable[A](tag, schemaName, tableName)
  with SavingMethods[I, A, IdTable[I, A]] {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[I]) = this(tag, None, tableName)

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

/**
 * Base trait for all tables. If you want to add some helpers methods for tables, here is the place.
 *
 * @param schemaName name of schema (optional)
 * @param tableName name of the table
 * @tparam A type of table
 * @author Krzysztof Romanowski, Jerzy Müller
 */
abstract class BaseTable[A](tag: Tag, schemaName: Option[String], tableName: String)
    extends Table[A](tag, schemaName, tableName)
    with CustomTypeMappers {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tag: Tag, tableName: String) = this(tag, None, tableName)
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
abstract class JunctionTable[A : BaseColumnType, B : BaseColumnType](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[(A, B)](tag, schemaName, tableName) {

  /**
   * Auxiliary constructor without schema name.
   * @param tableName name of table
   */
  def this(tag: Tag, tableName: String) = this(tag, None, tableName)

  /** Type mapper for A type */
  val aMapper = implicitly[BaseColumnType[A]]

  /** Type mapper for B type */
  val bMapper = implicitly[BaseColumnType[B]]

  /**
   * instead of def * = colA ~ colB write def columns = colA -> colB
   * @return
   */
  def columns: (Column[A], Column[B])

  def * = (columns._1, columns._2)

  def uniqueValues = index(s"${tableName}_uniq_idx", *, unique = true)
}