package controllers

import play.api._
import play.api.mvc._

import models._

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

}