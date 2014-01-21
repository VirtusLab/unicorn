package org.virtuslab.unicorn.ids

// TODO - change to play-slick
import scala.slick.driver.PostgresDriver.simple._

/**
 * Helper methods for saving entity.
 * This could be rewritten to a macro.
 *
 * @author krzysiek
 */
private[ids] trait SavingMethods[I <: BaseId, A <: WithId[I], T <: IdTable[I, A]] {
  self: T =>

  private def Error(value: A) = new RuntimeException(s"Unapply in saveBase returned None. Value: $value")

  protected def saveBase[B](proj: Column[B], unapply: A => Option[(Option[I], B)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2](proj: Projection2[B1, B2], unapply: A => Option[(Option[I], B1, B2)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3](proj: Projection3[B1, B2, B3], unapply: A => Option[(Option[I], B1, B2, B3)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4](proj: Projection4[B1, B2, B3, B4], unapply: A => Option[(Option[I], B1, B2, B3, B4)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5](proj: Projection5[B1, B2, B3, B4, B5], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6](proj: Projection6[B1, B2, B3, B4, B5, B6], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7](proj: Projection7[B1, B2, B3, B4, B5, B6, B7], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8](proj: Projection8[B1, B2, B3, B4, B5, B6, B7, B8], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9](proj: Projection9[B1, B2, B3, B4, B5, B6, B7, B8, B9], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10](proj: Projection10[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11](proj: Projection11[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12](proj: Projection12[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13](proj: Projection13[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14](proj: Projection14[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15](proj: Projection15[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16](proj: Projection16[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17](proj: Projection17[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18](proj: Projection18[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19](proj: Projection19[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20](proj: Projection20[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

  protected def saveBase[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20, B21](proj: Projection21[B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20, B21], unapply: A => Option[(Option[I], B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12, B13, B14, B15, B16, B17, B18, B19, B20, B21)])(v: A)(implicit s: Session): I =
    unapply(v).map(tuple => (proj returning id) insert TupleUtils.reduce(tuple)).getOrElse(throw Error(v))

}

