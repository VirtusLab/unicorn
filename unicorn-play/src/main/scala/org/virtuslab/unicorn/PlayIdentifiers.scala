package org.virtuslab.unicorn

import play.api.data.format.{ Formats, Formatter }
import play.api.mvc.QueryStringBindable.Parsing
import play.api.mvc.{ QueryStringBindable, PathBindable }

import scala.slick.lifted.MappedToBase

protected[unicorn] trait PlayIdentifiers extends Identifiers {
  self: HasJdbcDriver =>

  abstract class PlayCompanion[Id <: MappedToBase]
    extends CoreCompanion[Id]
    with Applicable[Id]
    with PlayImplicits[Id]

  /** Marker trait */
  protected[unicorn] trait Applicable[Id <: MappedToBase] {

    /**
     * Factory method for I instance creation.
     * @param id long from which I instance is created
     * @return I instance
     */
    def apply(id: Id#Underlying): Id
  }

  /**
   * Implicits required by Play.
   *
   * @tparam Id type of Id
   */
  protected[unicorn] trait PlayImplicits[Id <: MappedToBase] {
    self: Applicable[Id] =>

    /**
     * Type mapper for route files.
     * @param underlyingBinder path bindable for Id#Underlying type.
     * @return path bindable for I
     */
    implicit final def pathBindable(implicit underlyingBinder: PathBindable[Id#Underlying]): PathBindable[Id] =
      underlyingBinder.transform(apply, _.value)

    /** Implicit for mapping id to routes params for play */
    implicit final def toPathBindable(implicit bindable: Parsing[Id#Underlying]): QueryStringBindable[Id] =
      bindable.transform(apply, _.value)

    /** Form formatter for I */
    implicit final def idMappingFormatter(implicit underlyingFormat: Formatter[Id#Underlying]): Formatter[Id] = new Formatter[Id] {

      override val format = Some(("format.numeric", Nil))

      override def bind(key: String, data: Map[String, String]) =
        underlyingFormat.bind(key, data).right.map(apply).left.map {
          case errors if data.get(key).forall(_.isEmpty) => errors.map(_.copy(messages = Seq("id.empty")))
          case errors => errors.map(_.copy(messages = Seq("id.invalid")))
        }

      override def unbind(key: String, value: Id): Map[String, String] =
        Map(key -> value.value.toString)
    }
  }

}
