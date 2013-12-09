package controllers.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._

object RestCards extends Controller {

  def createCard(sessionId: Long, hatId: Long) = Action(parse.json) {
    Logger.debug("RestCards.addCard")
    request =>
      request.body \ "name" match {
        case _: JsUndefined => BadRequest(Json.obj("error" -> true,
          "message" -> "Could not match name =(")).as("application/json")
        case JsString(name) =>
          if (name == "content") {
            request.body \ "value" match {
              case _: JsUndefined => BadRequest(Json.obj("error" -> true,
                "message" -> "Could not match value =(")).as("application/json")
              case JsString(content) =>
                val user = User.dummyUser1;
                val hat = Hat.getById(hatId)
                Card.create(content, sessionId, hatId, user.id)

                val json = Json.obj(
                  "status" -> 200,
                  "fn" -> "createCard",
                  "args" -> Json.obj(
                    "content" -> content,
                    "user" -> user.name,
                    "thinkingSession" -> sessionId,
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