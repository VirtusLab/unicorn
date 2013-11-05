package play.api.db.slick.ids

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
