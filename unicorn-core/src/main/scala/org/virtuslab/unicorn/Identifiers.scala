package org.virtuslab.unicorn

protected[unicorn] trait Identifiers {
  self: HasJdbcDriver =>

  import driver.simple._

  /**
   * Base trait for all ids in system.
   * It is existential trait so it can have only defs.
   */
  trait BaseId extends Any with MappedTo[Long] {
    def id: Long

    override def value = id
  }

  /**
   * Base class for companion objects for id classes.
   * Adding this will allow you not to import mapping from your table class every time you need it.
   *
   * @tparam Id type of Id
   */
  abstract class CoreCompanion[Id <: BaseId] {

    /** Ordering for ids - it is normal simple ordering on inner longs ascending */
    implicit final lazy val ordering: Ordering[Id] = Ordering.by[Id, Long](_.id)
  }

  /**
   * Base class for all entities that contains an id.
   *
   * @tparam Id type of Id
   */
  trait WithId[Id] {

    /** @return id of entity (optional, entities does not have ids before save) */
    def id: Option[Id]
  }

}
