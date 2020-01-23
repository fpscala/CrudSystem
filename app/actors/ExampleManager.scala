package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import dao.ExampleDao
import javax.inject.Inject
import play.api.Environment
import protocols.ExampleProtocol._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class ExampleManager @Inject()(val environment: Environment,
                               exampleDao: ExampleDao
                              )
                              (implicit val ec: ExecutionContext)
  extends Actor with ActorLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def receive = {
    case Create(data) =>
      log.warning(s"menagerga keldi: $data")
      create(data).pipeTo(sender())
    //
    //    case Update(group) =>
    //      update(group).pipeTo(sender())
    //
    //    case Delete(id) =>
    //      delete(id).pipeTo(sender())

        case GetList =>
          getList.pipeTo(sender())

    case _ => log.info(s"received unknown message")
  }

  private def create(data: Example): Future[Int] = {
    log.warning(s"daoga yuborildi: $data")
    exampleDao.create(data)
  }
  private def getList: Future[Seq[Example]] = {
    exampleDao.getAll
  }
}
