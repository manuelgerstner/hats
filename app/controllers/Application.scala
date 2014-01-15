package controllers

import models._
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
  def index = Action { implicit request =>
    Logger.debug("Application.index")
    val cook = request.cookies.get(User.idCookie)
    val user: User = (request.cookies.get(User.idCookie) match {
      case Some(cookie) => User.byCookie(cookie);
      case None => User.byId(User.create("New User"));
    }) match {
      case Some(u) => u
      case None => User.dummy
    }

    Ok(views.html.index("Six Thinking Hats", user, sessionConfigForm)).withCookies(Cookie(User.idCookie, user.id.toString))
  }

  def javascriptRoutes = Action { implicit request =>
    Logger.debug("Application.jsRoutes")
    import play.api.Routes

    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.ThinkingSessions.restChangeHat)).as("text/javascript")
  }

}