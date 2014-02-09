package org.virtuslab.unicorn.ids.repositories

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Shape._
import org.virtuslab.unicorn.ids.JunctionTable

/**
* Base queries for junction tables
* @tparam A type of one entity
* @tparam B type of other entity
* @author Krzysztof Romanowski
*/
private[repositories] trait JunctionQueries[A, B] {

  protected def table: JunctionTable[A, B]

  protected def query: TableQuery[JunctionTable[A, B]]

  private implicit def aImpl = table.aMapper

  private implicit def bImpl = table.bMapper

  protected val getQuery = for {
    (a, b) <- Parameters[(A, B)]
    en <- query if en.columns._1 === a && en.columns._2 === b
  } yield en

  protected val getByAQuery = for {
    a <- Parameters[A]
    en <- query if en.columns._1 === a
  } yield en.columns._2

  protected val getByBQuery = for {
    b <- Parameters[B]
    en <- query if en.columns._2 === b
  } yield en.columns._1

  protected def getByAQueryFunc(a: A) = for {
    en <- query if en.columns._1 === a
  } yield en

  protected def getByBQueryFunc(b: B) = for {
    en <- query if en.columns._2 === b
  } yield en

  protected def getQueryFunc(a: A, b: B) = for {
    en <- query if en.columns._1 === a && en.columns._2 === b
  } yield en

}

/**
* Repository with basic methods for junction tables.
* @tparam A type of one entity
* @tparam B type of other entity
* @author Krzysztof Romanowski
*/
trait JunctionRepository[A, B] extends JunctionQueries[A, B] {

  /**
   * Deletes one element.
   *
   * @param elem element
   * @param session implicit session
   * @return number of deleted elements (0 or 1)
   */
  def delete(elem: (A, B))(implicit session: Session): Int =
    getQueryFunc(elem._1, elem._2).delete

  /**
   * Checks if element exists in database.
   *
   * @param elem element to check for
   * @param session implicit database session
   * @return true if element exists in database
   */
  def exists(elem: (A, B))(implicit session: Session) =
    getQueryFunc(elem._1, elem._2).firstOption.isDefined

  /**
   * @param session implicit session param for query
   * @return all elements of type (A, B)
   */
  def findAll()(implicit session: Session): Seq[(A, B)] = Query(table).list()

  /**
   * Saves one element if it's not present in db already.
   *
   * @param a one element
   * @param b other element
   * @param session implicit session
   * @return Option(elementId)
   */
  def save(a: A, b: B)(implicit session: Session) {
    getQuery(a, b)
      .firstOption()
      .orElse(Some(table.insert((a, b))))
  }

  /**
   * @param a element to query by
   * @return all b values for given a
   */
  def forA(a: A)(implicit session: Session): Seq[B] = getByAQuery(a).list()

  /**
   * @param b element to query by
   * @return all a values for given b
   */
  def forB(b: B)(implicit session: Session): Seq[A] = getByBQuery(b).list()

  /**
   * Delete all rows with given a value.
   * @param a element to query by
   */
  def deleteForA(a: A)(implicit session: Session): Int = {
    getByAQueryFunc(a).delete
  }

  /**
   * Delete all rows with given b value.
   * @param b element to query by
   */
  def deleteForB(b: B)(implicit session: Session): Int = {
    getByBQueryFunc(b).delete
  }

}
