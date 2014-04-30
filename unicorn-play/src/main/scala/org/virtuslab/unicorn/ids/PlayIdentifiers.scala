package org.virtuslab.unicorn.ids

import scala.slick.driver.JdbcDriver
import play.api.mvc.{QueryStringBindable, PathBindable}
import play.api.data.format.{Formats, Formatter}

/**
 * Created by Łukasz Dubiel on 30.04.14.
 */
trait PlayIdentifiers extends Identifiers {
  self: JdbcDriver =>

  abstract class PlayCompanion[I <: BaseId] extends CoreCompanion[I] with Applicable[I] with PlayImplicits[I]

  /** Marker trait */
  protected[unicorn] trait Applicable[I <: BaseId] {

    /**
     * Factory method for I instance creation.
     * @param id long from which I instance is created
     * @return I instance
     */
    def apply(id: Long): I
  }

  /**
   * Implicits required by Play.
   *
   * @tparam I type of Id
   * @author Krzysztof Romanowski, Jerzy Müller
   */
  protected[unicorn] trait PlayImplicits[I <: BaseId] {
    self: Applicable[I] =>

    /**
     * Type mapper for route files.
     * @param longBinder path bindable for Long type.
     * @return path bindable for I
     */
    implicit def pathBindable(implicit longBinder: PathBindable[Long]): PathBindable[I] =
      longBinder.transform(apply, _.id)

    /** Implicit for mapping id to routes params for play */
    implicit val toPathBindable: QueryStringBindable[I] =
      QueryStringBindable.bindableLong.transform(apply, _.id)

    /** Form formatter for I */
    implicit lazy val idMappingFormatter: Formatter[I] = new Formatter[I] {

      override val format = Some(("format.numeric", Nil))

      override def bind(key: String, data: Map[String, String]) =
        Formats.longFormat.bind(key, data).right.map(apply).left.map {
          case errors if data.get(key).forall(_.isEmpty) => errors.map(_.copy(message = "id.empty"))
          case errors => errors.map(_.copy(message = "id.invalid"))
        }

      override def unbind(key: String, value: I): Map[String, String] =
        Map(key -> value.id.toString)
    }
  }
}
