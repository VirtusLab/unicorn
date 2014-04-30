package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver


trait Identifiers {
  self: JdbcDriver =>

  import simple._

  /**
   * Base trait for all ids in system.
   * It is existential trait so it can have only defs.
   *
   * @author Krzysztof Romanowski, Jerzy Müller
   */
  trait BaseId extends Any with MappedTo[Long] {
    def id: Long

    override def value = id
  }

  /**
   * Base class for companion objects for id classes.
   * Adding this will allow you not to import mapping from your table class every time you need it.
   *
   * @tparam I type of Id
   * @author Krzysztof Romanowski, Jerzy Müller
   */
  abstract class CoreCompanion[I <: BaseId] {

    /** Ordering for ids - it is normal simple ordering on inner longs ascending */
    implicit lazy val ordering: Ordering[I] = Ordering.by[I, Long](_.id)
  }


  /**
   * Base class for all entities that contains an id.
   *
   * @author Krzysztof Romanowski
   */
  trait WithId[I] {

    /** @return id of entity (optional, entities does not have ids before save) */
    def id: Option[I]
  }

}