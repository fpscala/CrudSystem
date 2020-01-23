package dao

import java.util.Date

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.LazyLogging
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import protocols.ExampleProtocol.Example
import slick.jdbc.JdbcProfile
import utils.Date2SqlDate

import scala.concurrent.{ExecutionContext, Future}


trait ExampleComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import utils.PostgresDriver.api._

  class ExampleTable(tag: Tag) extends Table[Example](tag, "Example") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (id.?, name) <> (Example.tupled, Example.unapply _)
  }

}

@ImplementedBy(classOf[ExampleDaoImpl])
trait ExampleDao {
  def create(data: Example): Future[Int]

  def getAll: Future[Seq[Example]]

  def delete(id: Int): Future[Int]
}

@Singleton
class ExampleDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               val actorSystem: ActorSystem)
                              (implicit val ec: ExecutionContext)
  extends ExampleDao
    with ExampleComponent
    with HasDatabaseConfigProvider[JdbcProfile]
    with Date2SqlDate
    with LazyLogging {

  import utils.PostgresDriver.api._

  val examplesTable = TableQuery[ExampleTable]

  override def create(data: Example): Future[Int] = {
    db.run {
      logger.warn(s"daoga keldi: $data")
      (examplesTable returning examplesTable.map(_.id)) += data
    }
  }

  override def getAll: Future[Seq[Example]] = {
    db.run {
      examplesTable.result
    }
  }

  override def delete(id: Int): Future[Int] = {
    db.run{
      examplesTable.filter(_.id === id).delete
    }
  }
}

