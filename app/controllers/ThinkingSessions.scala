package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.mvc.Results
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import models._
import controllers._
//import views.html.defaultpages.badRequest
import wamplay.controllers.WAMPlayServer;
import views.html.defaultpages.notFound

/**
 * Controls all changes in ThinkingSession state.
 * @author Nemo
 */
object ThinkingSessions extends Controller {

  /**
   * Show the index of the current session
   */
  def index(id: Long) = Action { implicit request =>
    Logger.debug("ThinkingSessions.index")

    val user: User = request.cookies.get(User.idCookie) match {
      case Some(cookie) => User.byCookie(cookie).get;
      case None => User.byId(User.create("New User")).get;
    }

    ThinkingSession.byId(id) match {
      case Some(session) =>
        Ok(views.html.cards(session, Card.byThinkingSession(id), session.currentHat, user))
      case None =>
        NotFound
    }
  }

  /**
   * Update Session state to respective hat, show session index of new hat.
   */
  def changeHat(id: Long) = Action { implicit request =>
    Logger.debug("ThinkingSessions.changeHat")
    val user = request.cookies.get(User.idCookie) match {
      case Some(cookie) => User.byCookie(cookie).get;
      case None => User.byId(User.create("New User")).get;
    }
    ThinkingSession.byId(id) match {
      case Some(session) =>
        val nextHatId = HatFlow.nextDefaultHatId(session)
        ThinkingSession.changeHatTo(id, nextHatId)
        Ok(views.html.cards(session, Card.byThinkingSession(id), Hat.byId(nextHatId), user))
      case None =>
        NotFound
    }
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
      "mailAddresses" -> text)(SessionConfig.apply)(SessionConfig.unapply))

  /*
   * Create new Session
   */
  def createSession() = Action { implicit request =>
    Logger.debug("ThinkingSessions.createSession")
    val form = sessionConfigForm.bindFromRequest.get;
    request.cookies.get(User.idCookie) match {
      case Some(cookie) =>
        // TODO persist hatFlow
        val user = User.byCookie(cookie).get;
        val newSessionId = ThinkingSession.create(user, form.topic, Hat.dummy)
        WAMPlayServer.addTopic("thinkingSession_" + newSessionId)
        Logger.debug("Found user cookie, creating session " + newSessionId)
        Redirect(routes.ThinkingSessions.index(newSessionId))
      case None =>
        Logger.debug("No user cookie, bad request")
        BadRequest
    }
  }

  /**
   * Change the current Hat of a session. only owner (will be) allowed to do this
   */
  def restChangeHat(sessionId: Long) = Action { implicit request =>
    Logger.debug("ThinkingSessions.restChangeHat(" + sessionId + ")")
    request.cookies.get(User.idCookie) match {
      case Some(cookie) =>
        val user = User.byCookie(cookie).get;
        /// TODO Add check is user is owner later on
        ThinkingSession.byId(sessionId) match {
          case Some(session) =>
            val nextHatId = HatFlow.nextDefaultHatId(session)
            ThinkingSession.changeHatTo(sessionId, nextHatId)
            val nextHat = Hat.byId(nextHatId)
            Ok(Json.obj("hat" -> nextHat.name.toLowerCase)).as("application/json")
          case None => NotFound(Json.obj("error" -> "no session")).as("application/json")
        }
      case None => BadRequest(Json.obj("error" -> "no user")).as("application/json")
    }
  }

}