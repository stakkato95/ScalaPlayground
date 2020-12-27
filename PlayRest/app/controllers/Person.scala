package controllers

import play.api.libs.json._

case class Person(name: String, age: Int)

object Person {

  implicit val personWrites: OWrites[Person] = Json.writes[Person]
  implicit val personReads: Reads[Person] = Json.reads[Person]
}