package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

@Singleton
class ClusterController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getCluster: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok("hello world")
  }

  def postCluster: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    println(request.body)

    Json.fromJson[Person](request.body.asJson.get) match {
      case JsSuccess(value, path) =>
        println(s"success $value")
      case JsError(errors) =>
        println(s"error $errors")
    }

    Ok("hello world")
  }
}
