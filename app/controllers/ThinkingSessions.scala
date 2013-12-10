package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

import models._
//import models.forms._
import controllers._

/**
 * Controls all changes in ThinkingSession state.
 * @author Nemo
 */
object ThinkingSessions extends Controller {

  /**
   * Show the index of the current session
   */
  def index(id: Long) = Action {
    Logger.debug("ThinkingSessions.index")
    Ok(views.html.cards(id, Card.getThinkingSessionCards(id), ThinkingSession.getById(id).currentHat))
  }

  /**
   * Update Session state to respective hat, show session index of new hat.
   */
  def changeHat(id: Long) = Action {
    Logger.debug("ThinkingSessions.changeHat")
    val session = ThinkingSession.getById(id)
    val nextHatId = HatFlow.getNextDefaultHatId(ThinkingSession.getById(id))
    ThinkingSession.changeHatTo(id, nextHatId)
    Ok(views.html.cards(id, Card.getThinkingSessionCards(id), Hat.getById(nextHatId)))
  }

  /**
   * TODO: Conclude session and redirect to review page
   */
  def closeSession(id: Long) = TODO

  /**
   * Give a participant/user the opportunity to show she is ready to move on to the next hat.
   * Needed Form Params:
   * - UserID : Long
   *
   */
  def indicateReady(id: Long) = TODO

  /*
   * Save the configuration for hats
   * 
   */
  def saveConfig() = TODO

  /**
   * Change the current Hat of a session. only owner (will be) allowed to do this
   */
  def restChangeHat(sessionId: Long) = Action {
    Logger.debug("ThinkingSessions.restChangeHat(" + sessionId + ")")
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