package controllers

import controllers.ThinkingSessions.sessionConfigForm
import play.api._
import play.api.mvc._

import play.core.Router.JavascriptReverseRoute

/**
 * Reponsible for all ThinkingSession setup and creation.
 * If we ever add user accounts this controller should handle them too
 */
object Application extends Controller {

  /**
   * Landing Page
   */
  def index = Action {
    Logger.debug("Application.index")
    Ok(views.html.index("Six Thinking Hats", sessionConfigForm))
  }

  def javascriptRoutes = Action { implicit request =>
    Logger.debug("Application.jsRoutes")
    import play.api.Routes

    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.ThinkingSessions.restChangeHat
      )
    ).as("text/javascript")
  }

}