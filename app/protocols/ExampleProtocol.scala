package protocols

import java.util.Date

import play.api.libs.json.{Json, OFormat}

object ExampleProtocol {

  case object GetList

  case class Delete(id: Int)
  case class Update(update: Example)

  case class Create(data: Example)

  case class Example(id: Option[Int] = None,
                     name: String,
                    )
  implicit val studentFormat: OFormat[Example] = Json.format[Example]
}

