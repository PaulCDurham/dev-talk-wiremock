package models

import play.api.libs.json._

case class Person(
  id: Int,
  firstName: String,
  lastName: String
)

object Person {
  implicit val formats: OFormat[Person] = Json.format[Person]
}
