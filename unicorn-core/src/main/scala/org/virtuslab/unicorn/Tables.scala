package org.virtuslab.unicorn

import slick.lifted.Index
import slick.lifted.ProvenShape

protected[unicorn] trait Tables[Underlying] extends TypeMappers {
  self: HasJdbcProfile =>

  import profile.api._

  /**
   * Base class for all tables that contains an id.
   *
   * @param schemaName name of schema (optional)
   * @param tableName name of the table
   * @param mapping mapping for id of this table
   * @tparam Id type of id
   * @tparam Entity type of entities in table
   */
  abstract class IdTable[Id <: BaseId[Underlying], Entity <: WithId[Underlying, Id]](tag: Tag, schemaName: Option[String], tableName: String)(implicit val mapping: BaseColumnType[Id])
    extends BaseTable[Entity](tag, schemaName, tableName) {

    /**
     * Auxiliary constructor without schema name.
     * @param tableName name of table
     */
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[Id]) = this(tag, None, tableName)

    /**
     * Name of an `id` column - override it if you want to change it.
     *
     * For example in H2DB where you need an uppercase "ID":
     *
     * {{{
     *   override val idColumnName = "ID"
     * }}}
     */
    protected val idColumnName: String = "id"

    /** @return id column representation of this table */
    def id: Rep[Id] = column[Id](idColumnName, O.PrimaryKey, O.AutoInc)
  }

  /**
   * Base trait for all tables. If you want to add some helpers methods for tables, here is the place.
   *
   * @param schemaName name of schema (optional)
   * @param tableName name of the table
   * @tparam Entity type of entities in table
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
   */
  abstract class JunctionTable[First: BaseColumnType, Second: BaseColumnType](tag: Tag, schemaName: Option[String], tableName: String)
    extends Table[(First, Second)](tag, schemaName, tableName) {

    /**
     * Auxiliary constructor without schema name.
     * @param tableName name of table
     */
    def this(tag: Tag, tableName: String) = this(tag, None, tableName)

    /**
     * instead of def * = colA ~ colB write def columns = colA -> colB
     * @return
     */
    def columns: (Rep[First], Rep[Second])

    final def * : ProvenShape[(First, Second)] = (columns._1, columns._2)

    final def uniqueValues: Index = index(s"${tableName}_uniq_idx", *, unique = true)
  }
}
