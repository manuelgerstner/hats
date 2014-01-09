package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

import models._
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
   * val to initiate session
   */
  val sessionConfigForm: Form[SessionConfig] = Form(
    mapping(
      "topic" -> nonEmptyText,
      "whiteTimeLimit" -> optional(number),
      "whiteAloneTime" -> optional(number),
      "yellowTimeLimit" -> optional(number),
      "yeellowAloneTime" -> optional(number),
      "redTimeLimit" -> optional(number),
      "redAloneTime" -> optional(number),
      "greenTimeLimit" -> optional(number),
      "greenAloneTime" -> optional(number),
      "blueTimeLimit" -> optional(number),
      "blueAloneTime" -> optional(number),
      "blackTimeLimit" -> optional(number),
      "blackAloneTime" -> optional(number),
      "mailAddresses" -> text
    )(SessionConfig.apply)(SessionConfig.unapply))

  /*
   * Save the configuration for hats
   * 
   */
  def createSession() = Action { implicit request =>

    val form = sessionConfigForm.bindFromRequest.get;

    // TODO
    // - identify user
    // - create session
    // - persist hatFlow
    Redirect(routes.ThinkingSessions.index(1))
  }

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
    Ok(Json.obj("hat" -> nextHat.name.toLowerCase)).as("application/json")
  }

}