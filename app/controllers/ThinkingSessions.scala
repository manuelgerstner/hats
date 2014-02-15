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
import com.feth.play.module.mail.Mailer
import com.feth.play.module.mail.Mailer.Mail.Body
import views.html.defaultpages.notFound
import scala.collection.JavaConversions
import java.util.Date
import ws.wamplay.controllers.WAMPlayServer
import play.templates.BaseScalaTemplate

/**
 * Controls all changes in ThinkingSession state.
 * @author Nemo
 */
object ThinkingSessions extends Controller with UserCookieHandler {

  /**
   * Show the index of the current session
   */
  def index(id: Long) = Action { implicit request =>
    Logger.debug("ThinkingSessions.index")
    val user = cookieUser(request)
    ThinkingSession.byId(id) match {
      case Some(session) => // session exists
        if (ThinkingSession.checkUser(session, user)) { // check if user is part of session
          if (session isRunning)
            Ok(views.html.cards(session, Card.byThinkingSession(id), session.currentHat, user.get, Event.byThinkingSession(id)))
          else
            Redirect(routes.Dashboard.showReport(id))
        } else
          Unauthorized
      case None =>
        NotFound
    }

  }

  def join(id: Long, token: String) = Action {
    val t: Long = java.lang.Long.parseLong(token, 16)
    ThinkingSession.checkJoinToken(id, t) match {
      case Some(userId) => // set token
        Logger.debug("User " + userId + " joined session " + id)
        val session = ThinkingSession.byId(id).get;
        val user = User.byId(userId)
        val eventId = Event.create("userJoin", session, session.currentHat, user, None, None, new Date())
        val event = Event.byId(eventId)
        WebSocket.publishEvent(event, id)

        Redirect(routes.ThinkingSessions.index(id)).withCookies(Cookie(User.idCookie, userId.toString))
      case None =>
        BadRequest
    }
  }

  /**
   * Closes the session and publishes the a corresponding WebSocket event.
   * Redirects to Dashboard controller
   */
  def closeSession(id: Long) = Action { implicit request =>
    val userOption = cookieUser(request)
    val sessionOption = ThinkingSession.byId(id)

    if (ThinkingSession.checkUser(sessionOption, userOption)) {
      // persist finished state
      val session = sessionOption.get;
      ThinkingSession.finish(session);

      // publish event about the finished session
      val eventId = Event.create("closeSession", session, session.currentHat, userOption, None, None, new Date())
      val event = Event.byId(eventId);
      WebSocket.publishEvent(event, id);

      Redirect(routes.Dashboard.showReport(id))
    } else {
      Unauthorized
    }
  }

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
      "adminMailAddress" -> optional(text),
      "mailAddresses" -> text)(SessionConfig.apply)(SessionConfig.unapply))

  /*
   * Create new Session
   */
  def createSession() = Action { implicit request =>
    Logger.debug("ThinkingSessions.createSession")
    val form = sessionConfigForm.bindFromRequest.get;

    cookieUser(request) match {
      case Some(creator) =>
        val newSessionId = ThinkingSession.create(creator, form.topic, Hat.dummy)
        if (creator.mail == None) {
          User.saveMail(creator, form.adminMail.get)
        }
        ThinkingSession.byId(newSessionId) match {
          case Some(session) =>
            // add creator
            val token = ThinkingSession.addUser(session, creator)
            sendCreatorMail(creator, token, form.topic, session)
            // add all invitees

            val mailsAndTokens = addUsersToSessions(form.mailAddressList, newSessionId)
            sendInviteMails(mailsAndTokens, form.topic, newSessionId)

            // publish websocket event
            Event.create("createSession", session, session.currentHat, Some(creator), None, None, new Date())
            WAMPlayServer.addTopic(newSessionId.toString)

            Logger.debug("Creating session " + newSessionId)
            Redirect(routes.ThinkingSessions.index(newSessionId))
          case None =>
            Logger.error("Session creation Failed")
            InternalServerError
        }

      case None => BadRequest
    }

  }

  def sendInviteMails(mails: List[(String, Long)], title: String, sessionId: scala.Long)(implicit request: Request[AnyContent]) {
    mails match {
      case (m, t) :: ms =>

        val url = routes.ThinkingSessions.join(sessionId, toHexString(t)).absoluteURL(false)(request)
        val body = new Body(views.txt.email.invite.render(title, url).toString(),
          views.html.email.invite.render(title, url).toString());
        Mailer.getDefaultMailer().sendMail("Invite to Thinking Session", body, m);
        Logger.debug("Invited User " + m + " to thinking session " + title)
        sendInviteMails(ms, title, sessionId)
      case Nil =>
        Logger.info("All Users invited to session " + title)
    }
  }

  def toHexString(l: Long): String = if (l < 0l) "-" + (-1 * l).toHexString else l.toHexString

  def sendCreatorMail(creator: User, token: Long, title: String, session: ThinkingSession)(implicit request: Request[AnyContent]) {
    creator.mail match {
      case Some(mail) =>
        val url = routes.ThinkingSessions.join(session.id, toHexString(token)).absoluteURL(false)(request)
        val body = new Body(views.txt.email.creator.render(title, url).toString(),
          views.html.email.creator.render(title, url).toString());
        Mailer.getDefaultMailer().sendMail("Invite to Thinking Session", body, mail);
        Logger.debug("Invited Creator " + creator.mail + " to thinking session " + title)
      case None =>
        Logger.info("Creator could not be invited, no mail address found " + title)
    }
  }

  /**
   * returns a list of (mail,token)
   */
  def addUsersToSessions(mails: List[String], sessionId: Long): List[(String, Long)] = {
    mails match {
      case m :: ms =>
        val token: Long = User.byMail(m) match {
          case Some(u) => // user already exists
            Logger.debug("Adding existing user with mail " + m)
            ThinkingSession.addUser(sessionId, u.id)
          case None => // create new user
            Logger.debug("Adding new user with mail " + m)
            ThinkingSession.addUser(sessionId, User.create("New User", Some(m)))
        }
        (m, token) :: addUsersToSessions(ms, sessionId)
      case Nil => Nil
    }
  }
}