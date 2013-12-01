package controllers

import play.api._
import play.api.mvc._

/**
 * Reponsible for all ThinkingSession setup and creation.
 * If we ever add user accounts this controller should handle them too
 */
object Application extends Controller {

  /**
   * TODO: Homepage, offer ThinkingSession creation and joining a running session (only if allowed, invited)
   */
  def index = Action {
    Ok(views.html.test("Your new application is ready."))
  }

  /**
   * Used for testing
   */
  def test = Action {
    Ok(views.html.test("Welcome to the Six Thinking Hats!"))
  }
  
  def graph = Action {
    Ok(views.html.graph("This is gonna be the process outline"))
  }

}