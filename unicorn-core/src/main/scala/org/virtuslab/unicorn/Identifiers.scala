package org.virtuslab.unicorn

import scala.slick.lifted.{ MappedToBase, MappedTo }

trait Identifiers {

  /**
   * Base trait for all Long ids in system.
   */
  trait BaseId extends Any with MappedId[Long]

  /**
   * Base trait for implementing ids.
   * It is existential trait so it can have only defs.
   */
  trait MappedId[T] extends Any with MappedTo[T] {
    def id: T
    override def value = id
  }

  /**
   * Base class for companion objects for id classes.
   * Adding this will allow you not to import mapping from your table class every time you need it.
   *
   * @tparam Id type of Id
   */
  abstract class CoreCompanion[Id <: MappedToBase] {

    /** Ordering for ids */
    implicit def basic_ordering(implicit ord: Ordering[Id#Underlying]) = Ordering.by[Id, Id#Underlying](_.value)(ord)
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
