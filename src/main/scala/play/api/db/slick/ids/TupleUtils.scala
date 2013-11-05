package play.api.db.slick.ids

import scala.language.postfixOps

/**
 * Some helper methods for working on tuples.
 * This could be rewritten to a macro.
 *
 * @author Krzysztof Romanowski
 */
private[ids] object TupleUtils {

  def reduce[R, A1](t: (R, A1)): (A1) = (t._2)

  def reduce[R, A1, A2](t: (R, A1, A2)): (A1, A2) = (t._2, t._3)

  def reduce[R, A1, A2, A3](t: (R, A1, A2, A3)): (A1, A2, A3) = (t._2, t._3, t._4)

  def reduce[R, A1, A2, A3, A4](t: (R, A1, A2, A3, A4)): (A1, A2, A3, A4) = (t._2, t._3, t._4, t._5)

  def reduce[R, A1, A2, A3, A4, A5](t: (R, A1, A2, A3, A4, A5)): (A1, A2, A3, A4, A5) = (t._2, t._3, t._4, t._5, t._6)

  def reduce[R, A1, A2, A3, A4, A5, A6](t: (R, A1, A2, A3, A4, A5, A6)): (A1, A2, A3, A4, A5, A6) = (t._2, t._3, t._4, t._5, t._6, t._7)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7](t: (R, A1, A2, A3, A4, A5, A6, A7)): (A1, A2, A3, A4, A5, A6, A7) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8](t: (R, A1, A2, A3, A4, A5, A6, A7, A8)): (A1, A2, A3, A4, A5, A6, A7, A8) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9)): (A1, A2, A3, A4, A5, A6, A7, A8, A9) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21)

  def reduce[R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21](t: (R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21)): (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) = (t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19, t._20, t._21, t._22)

}
