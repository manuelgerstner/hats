package controllers.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._

/**
 * Rest endpoint to do all actions that the TinkingSessions controller offers from AJAX
 */
object RestThinkingSessions extends Controller {

  /**
   * Change the current Hat of a session. only owner (will be) allowed to do this
   */
  def changeHat(sessionId: Long) = Action {
    Logger.debug("RestThinkingSession.changeHat(" + sessionId + ")")
    val user = User.dummyUser1; // TODO get from session and compare to owner later on
    val session = ThinkingSession.getById(sessionId)
    val nextHatId = HatFlow.getNextDefaultHatId(session)
    ThinkingSession.changeHatTo(sessionId, nextHatId)
    val nextHat = Hat.getById(nextHatId)
    val json = Json.obj(
      "status" -> 200,
      "fn" -> "changeHat",
      "args" -> Json.obj(
        "user" -> user.name,
        "thinkingSession" -> sessionId,
        "hat" -> nextHat.name
      )
    )
    Ok(Json.obj("content" -> json)).as("application/json")
  }
}