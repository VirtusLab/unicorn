package org.virtuslab.unicorn

import org.virtuslab.unicorn.utils.Invoker
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Application }
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import play.api.db.slick.{ SlickApi, DbName, SlickModule, DatabaseConfigProvider }
import play.api.libs.json.Format
import play.api.mvc.{ PathBindable, QueryStringBindable }
import slick.backend.DatabaseConfig
import slick.driver.JdbcDriver
import slick.lifted.{ AppliedCompiledFunction, Rep }
import slick.profile.BasicProfile

import scala.language.implicitConversions

trait UnicornPlayLike[Underlying]
    extends Unicorn[Underlying]
    with PlayIdentifiers[Underlying]
    with HasJdbcDriver {

  def underlyingFormatter: Formatter[Underlying]

  def underlyingFormat: Format[Underlying]

  def underlyingQueryStringBinder: QueryStringBindable[Underlying]

  def underlyingPathBinder: PathBindable[Underlying]

  //  override lazy val driver: JdbcDriver = DatabaseConfigProvider.get(play.api.Play.current).driver
  override lazy val driver: JdbcDriver = getDbDriver[JdbcDriver].driver

  def getDbDriver[P <: BasicProfile](): DatabaseConfig[P] = {
    val dbName = configuration.underlying.getString(SlickModule.DefaultDbName)
    slickApi.dbConfig[P](DbName(dbName))
  }

  //  @throws(classOf[IllegalArgumentException])
  //  def apply[P <: BasicProfile](implicit app: Application): DatabaseConfig[P] = {
  //    val defaultDbName =
  //      this (defaultDbName)
  //  }
  //
  //  @throws(classOf[IllegalArgumentException])
  //  def apply[P <: BasicProfile](dbName: String)(implicit app: Application): DatabaseConfig[P] =

  override type IdCompanion[Id <: BaseId] = PlayCompanion[Id]

  //  implicit def syncRep[T](rep: Rep[T]): SyncRep[T] = new SyncRep(rep, driver)

  import driver.api._

  implicit class SyncAcf[R, RU, EU, C[_]](acf: AppliedCompiledFunction[_, Query[R, EU, C], RU]) extends Invoker {
    final def firstOption(implicit session: Session): Option[EU] = {
      invokeAction(acf.result.headOption)
    }
  }

  def environment: Environment

  def configuration: Configuration

  def applicationLifecycle: ApplicationLifecycle

  def slickApi: SlickApi

}

class UnicornPlay[Underlying](implicit val underlyingFormatter: Formatter[Underlying],
  val underlyingFormat: Format[Underlying],
  val underlyingQueryStringBinder: QueryStringBindable[Underlying],
  val underlyingPathBinder: PathBindable[Underlying],
  val ordering: Ordering[Underlying])
    extends UnicornPlayLike[Underlying] {

  var _environment: Environment = null
  var _configuration: Configuration = null
  var _applicationLifecycle: ApplicationLifecycle = null
  var _slickApi: SlickApi = null

  override def environment: Environment = _environment

  override def configuration: Configuration = _configuration

  override def applicationLifecycle: ApplicationLifecycle = _applicationLifecycle

  override def slickApi = _slickApi

}

object LongUnicornPlay extends UnicornPlay[Long]
