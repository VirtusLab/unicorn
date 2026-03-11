package org.virtuslab.unicorn

import slick.jdbc.JdbcProfile

import scala.reflect.ClassTag

class IsomorphicColumnTypeConversion(implicit val profile: JdbcProfile) {
  import profile._
  implicit def isomorphicType[A, B](implicit iso: Isomorphism[A, B], ct: ClassTag[A], jt: BaseColumnType[B]): BaseColumnType[A] =
    MappedColumnType.base[A, B](iso.map, iso.comap)
}
