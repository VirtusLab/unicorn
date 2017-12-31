package org.virtuslab.unicorn.dsl

import org.virtuslab.unicorn.BaseId
import org.virtuslab.unicorn.HasJdbcProfile
import org.virtuslab.unicorn.Identifiers
import org.virtuslab.unicorn.Unicorn
import org.virtuslab.unicorn.WithId
import slick.lifted.TableQuery

trait UnicornDSL[Underlying] { self: Unicorn[Underlying] with HasJdbcProfile =>
  import profile.api.BaseColumnType

  /**
   * Basic DSL designed to not automate as much as possible in creating new DB entities
   * This trait is basic version that wires all types where `EntityDsl` has pre-generated Id and IdCompanions.
   *
   * See `EntityDsl` docs for more information.
   */
  trait EntityDSLBase {
    /** Id type that will be use in this Entity */
    type Id <: BaseId[Underlying]

    /** Type of raw row of this entity. Intended to override by case class */
    type Row <: BaseRow

    /** Implantation detail. Wires Row with Id */
    final type BaseRow = WithId[Underlying, Id]

    /** Type that represents Table for this entity. Intended to be override with class. */
    type Table <: BaseTable

    /** Implantation detail. Wires Row and Id with Table */
    final type BaseTable = IdTable[Id, Row]

    /** Basic class for Repository in this entity */
    class DslRepository(override val query: TableQuery[Table])(override implicit val mapping: BaseColumnType[Id])
      extends BaseIdRepository[Id, Row, Table](query)

    /** Repository for this entity. */
    val Repository: DslRepository

    /** Table query for this entity */
    def query: TableQuery[Table] = Repository.query
  }

  /**
   * This is basic class to create your Entity. Entity here means database table, row and dedicated basic repository
   * together with helper classes such as unique Id type.
   * Normally, you need to generate multiple classes/objects, remember to specify correct types etc.
   * Generally a lot of boilerplate.
   * Using Entity Dsl all you need is:
   * 1. Create object that extends from `EntityDsl` with proper name (e.g. User, Invoice)
   * 2. Create inside case class `Row` representing raw row of data with one filed called `id` of type `Option[Id]`
   * 3. Create inside class Table extending `BaseTable` that represents your table (with Tag and table name).
   *    Inside Unicorn generate definition for `id` that needs to be added to `*` projection as `id.?`
   * 4. Last thing is to implement value `Repository` with new instance of `DslRepository`.
   *
   * Minimal entity looks like this:
   *
   * ```
   * object User extends EntityDsl(myProfile){
   *   case class Row(id: Option[Id], name: String)
   *   class Table(tag: Tag) extends BaseTable(tag, "Table_Name"){
   *     def name = column[String]("FIRST_NAME")
   *     override def * = (id.?, name).mapTo[Row]
   *   }
   *   override val Repository = new DslRepository(TableQuery[Table])
   * }
   * ```
   * There still some boilerplate (e.g. `new DslRepository(TableQuery[Table])`, `.mapTo[Row]`)
   * and we plan to elimitate also those in future.
   *
   * This approach changes slightly the way how you work with your entity. Instead of using e.g. `UserRow` or
   * `UsersRepository` now `User.Row` and `User.Repository` should be used.
   * Another benefit of this approach is that now you can abstract some pieces of code around entity such as schema
   * creation.
   *
   * @param identifiers identifiers instance use together with you Unicorn instance.
   */
  abstract class EntityDsl(val identifiers: Identifiers[Underlying]) extends EntityDSLBase {

    /** Pre-generated Id for this entity */
    case class Id(id: Underlying) extends BaseId[Underlying]

    /** Pre-generated Id companion for this entity */
    object Id extends identifiers.CoreCompanion[Id]
  }
}
