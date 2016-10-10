package org.virtuslab.unicorn

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.mvc.{ PathBindable, QueryStringBindable }

import play.api.libs.json._

protected[unicorn] trait PlayIdentifiers[Underlying] {
  self: PlayIdentifiersImpl[Underlying] with Identifiers[Underlying] =>

  abstract class PlayCompanion[Id <: BaseId[Underlying]]
    extends CoreCompanion[Id]
    with Applicable[Id]
    with PlayImplicits[Id]

  /** Marker trait */
  protected[unicorn] trait Applicable[Id <: BaseId[Underlying]] extends Any {

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
  protected[unicorn] trait PlayImplicits[Id <: BaseId[Underlying]] {
    self: Applicable[Id] =>

    /** Type mapper for route files. */
    implicit final val pathBinder: PathBindable[Id] = underlyingPathBinder.transform(apply, _.value)

    /** Implicit for mapping id to routes params for play */
    implicit final val queryStringBinder: QueryStringBindable[Id] = underlyingQueryStringBinder.transform(apply, _.value)

    /** Form formatter for Id */
    implicit final val idMappingFormatter: Formatter[Id] = new Formatter[Id] {

      override val format = Some(("format.numeric", Nil))

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Id] = {

        def handleErrors(errors: Seq[FormError]): Seq[FormError] = errors match {
          case _ if data.get(key).forall(_.isEmpty) => errors.map(_.copy(messages = Seq("id.empty")))
          case _ => errors.map(_.copy(messages = Seq("id.invalid")))
        }

        underlyingFormatter.bind(key, data)
          .right.map(apply)
          .left.map(handleErrors)
      }

      override def unbind(key: String, id: Id): Map[String, String] = underlyingFormatter.unbind(key, id.value)
    }

    /** Json format for Id */
    implicit final val idJsonFormat: Format[Id] = new Format[Id] {
      def reads(p1: JsValue): JsResult[Id] = underlyingFormat.reads(p1).map(apply)
      def writes(p1: Id): JsValue = underlyingFormat.writes(p1.value)
    }

  }

}
