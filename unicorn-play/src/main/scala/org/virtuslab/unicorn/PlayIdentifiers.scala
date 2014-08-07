package org.virtuslab.unicorn

import play.api.data.format.Formatter
import play.api.mvc.{ PathBindable, QueryStringBindable }

protected[unicorn] trait PlayIdentifiers[Underlying] extends Identifiers[Underlying] {
  self: UnicornPlay[Underlying] =>

  abstract class PlayCompanion[Id <: BaseId]
    extends CoreCompanion[Id]
    with Applicable[Id]
    with PlayImplicits[Id]

  /** Marker trait */
  protected[unicorn] trait Applicable[Id <: BaseId] extends Any {

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
  protected[unicorn] trait PlayImplicits[Id <: BaseId] {
    self: Applicable[Id] =>

    /** Type mapper for route files. */
    implicit final val pathBinder: PathBindable[Id] = underlyingPathBinder.transform(apply, _.value)

    /** Implicit for mapping id to routes params for play */
    implicit final val queryStringBinder: QueryStringBindable[Id] = underlyingQueryStringBinder.transform(apply, _.value)

    /** Form formatter for Id */
    implicit final val idMappingFormatter: Formatter[Id] = new Formatter[Id] {

      override val format = Some(("format.numeric", Nil))

      override def bind(key: String, data: Map[String, String]) =
        underlyingFormat.bind(key, data).right.map(apply).left.map {
          case errors if data.get(key).forall(_.isEmpty) => errors.map(_.copy(messages = Seq("id.empty")))
          case errors => errors.map(_.copy(messages = Seq("id.invalid")))
        }

      override def unbind(key: String, id: Id): Map[String, String] = underlyingFormat.unbind(key, id.value)
    }
  }

}
