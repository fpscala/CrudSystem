package controllers

import akka.actor.ActorRef
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import javax.inject._
import org.webjars.play.WebJarsUtil
import play.api.mvc._
import protocols.ExampleProtocol.{Create, Delete, Example, GetList, Update}
import views.html._
import akka.pattern.ask
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val webJarsUtil: WebJarsUtil,
                              @Named("example-manager") val exampleManager: ActorRef,
                               indexTemplate: index
                              )
                              (implicit val ec: ExecutionContext)
  extends BaseController with LazyLogging {

  implicit val defaultTimeout: Timeout = Timeout(60.seconds)

  def index = Action {
    Ok(indexTemplate())
  }


  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").as[String]
    logger.warn(s"controllerga keldi")
    (exampleManager ? Create(Example(None, name))).mapTo[Int].map { id =>
        Ok(Json.toJson(id))
    }
  }

 def getNames: Action[AnyContent] = Action.async {
   (exampleManager ? GetList).mapTo[Seq[Example]].map{ name =>
     Ok(Json.toJson(name))
   }
 }

  def delete = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[Int]
    (exampleManager ? Delete(id)).mapTo[Int].map{ i =>
      if (i != 0){
        Ok(Json.toJson(id + " raqamli ism o`chirildi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }

  def update = Action.async(parse.json) { implicit request =>
    val id = (request.body \ "id").as[Int]
    val name = (request.body \ "name").as[String]
    (exampleManager ? Update(Example(Some(id), name))).mapTo[Int].map{ i =>
      if (i != 0){
        Ok(Json.toJson(id + " raqamli ism yangilandi"))
      }
      else {
        Ok("Bunday raqamli ism topilmadi")
      }
    }
  }
}
