package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver

trait Tables extends Identifiers with TypeMappers {
  self: JdbcDriver =>

  import profile.simple._

  /**
   * Base class for all tables that contains an id.
   *
   * @param schemaName name of schema (optional)
   * @param tableName name of the table
   * @param mapping mapping for id of this table
   * @tparam Id type of id
   * @tparam Entity type of entities in table
   */
  abstract class IdTable[Id <: BaseId, Entity <: WithId[Id]](tag: Tag, schemaName: Option[String], tableName: String)
                                                            (implicit val mapping: BaseColumnType[Id])
    extends BaseTable[Entity](tag, schemaName, tableName) {

    /**
     * Auxiliary constructor without schema name.
     * @param tableName name of table
     */
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[Id]) = this(tag, None, tableName)

    /** @return id column representation of this table */
    final def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
  }

  /**
   * Base trait for all tables. If you want to add some helpers methods for tables, here is the place.
   *
   * @param schemaName name of schema (optional)
   * @param tableName name of the table
   * @tparam Entity type of entities in table
   * @author Krzysztof Romanowski, Jerzy Müller
   */
  abstract class BaseTable[Entity](tag: Tag, schemaName: Option[String], tableName: String)
    extends Table[Entity](tag, schemaName, tableName)
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
   * @tparam First type of one entity
   * @tparam Second type of other entity
   * @author Krzysztof Romanowski, Jerzy Müller
   */
  abstract class JunctionTable[First: BaseColumnType, Second: BaseColumnType](tag: Tag, schemaName: Option[String], tableName: String)
    extends Table[(First, Second)](tag, schemaName, tableName) {

    /**
     * Auxiliary constructor without schema name.
     * @param tableName name of table
     */
    def this(tag: Tag, tableName: String) = this(tag, None, tableName)

    /** Type mapper for A type */
    val aMapper = implicitly[BaseColumnType[First]]

    /** Type mapper for B type */
    val bMapper = implicitly[BaseColumnType[Second]]

    /**
     * instead of def * = colA ~ colB write def columns = colA -> colB
     * @return
     */
    def columns: (Column[First], Column[Second])

    def * = (columns._1, columns._2)

    def uniqueValues = index(s"${tableName}_uniq_idx", *, unique = true)
  }

}