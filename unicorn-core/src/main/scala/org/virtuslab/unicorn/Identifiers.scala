package org.virtuslab.unicorn

import slick.lifted.MappedTo

/**
 * Base trait for implementing ids.
 * It is existential trait so it can have only defs.
 */
trait BaseId[U] extends Any with MappedTo[U] {
  def id: Underlying
  override def value: Underlying = id
}

/**
 * Base class for all entities that contains an id.
 * @tparam Id type of Id
 */
trait WithId[Underlying, Id <: BaseId[Underlying]] {

  /** @return id of entity (optional, entities does not have ids before save) */
  def id: Option[Id]
}

trait Identifiers[Underlying] {

  def ordering: Ordering[Underlying]

  import scala.language.higherKinds

  type IdCompanion[Id <: BaseId[Underlying]]

  /**
   * Base class for companion objects for id classes.
   * Adding this will allow you not to import mapping from your table class every time you need it.
   *
   * @tparam Id type of Id
   */
  abstract class CoreCompanion[Id <: BaseId[Underlying]] {

    /** Ordering for ids */
    implicit val basicOrdering = Ordering.by[Id, Id#Underlying](_.value)(ordering)
  }

}
