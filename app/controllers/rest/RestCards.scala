package controllers.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._

object RestCards extends Controller {

  def createCard(sessionId: Long, hatId: Long) = Action(parse.json) {
    request =>
      request.body \ "name" match {
        case _: JsUndefined => BadRequest(Json.obj("error" -> true,
          "message" -> "Could not match name =(")).as("application/json")
        case name: JsValue =>
          if (name == JsString("content")) {
            request.body \ "value" match {
              case _: JsUndefined => BadRequest(Json.obj("error" -> true,
                "message" -> "Could not match value =(")).as("application/json")
              case value: JsValue =>
                val user = User.getDummyUser1;
                val hat = Hat.getById(hatId)
                val session = ThinkingSession.dummy
                Card.create(value.toString, session, hat, user)

                val json = Json.obj(
                  "status" -> 200,
                  "fn" -> "createCard",
                  "args" -> Json.obj(
                    "content" -> value,
                    "user" -> user.name,
                    "thinkingSession" -> session.id,
                    "hat" -> hat.name
                  )
                )
                // Return reponse with 200 (OK) status and JSON body
                Ok(Json.obj("content" -> json)).as("application/json")
            }
          } else {
            BadRequest(Json.obj("error" -> true,
              "message" -> "Name was not content =(")).as("application/json")
          }
      }
  }
}