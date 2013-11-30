package controllers

import play.api._
import play.api.mvc._

object ThinkingSessions extends Controller {

  /**
   * TODO: Show the the dashboard for the current session
   */
  def index(id: Long) = Action {
    Ok(views.html.cards(id))
  }

  /**
   * TODO: Update Session state to respective hat, show session index of new hat.
   * Push info to all clients.
   * Needed Form Params:
   * HatID: Long
   */
  def changeHat(id: Long) = TODO

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