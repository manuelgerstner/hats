package controllers

import models.Card
import models.Hat
import models.User
import models.ThinkingSession
import models.forms.FormCard
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller

/**
 * Card Controller responsible for handling CRUD operations on ideas/cards (will be treated as synonyms,
 * as a card is merely the visual representation of an idea)
 *
 * @author: NemoOudeis
 */
object Cards extends Controller {

  /**
   * Form to create a card
   */
  val cardForm = Form(
    mapping(
      "content" -> nonEmptyText,
      "hat" -> nonEmptyText)(FormCard.apply)(FormCard.unapply))

  /**
   * Needed Info from HTML Params or (browser)session/user info:
   * - HatID : Long  		Dummy for now
   * - UserID : Long		Dummy for now
   * - content : String
   * Content needs to be a string obviously.
   * To use this endpoint see CardForm (below)
   */
  def addCard(thinkingSessionId: Long) = Action { implicit request =>
    Logger.debug("Cards.addCard")
    request.cookies.get(User.idCookie) match {
      case Some(cookie) => {
        val form = cardForm.bindFromRequest.get
        val user = User.byCookie(cookie).get;
        val cardId = Card.create(form, thinkingSessionId, User.dummyId, 0, 0, None, None)
        Logger.debug("Found user cookie, creating card " + cardId)
        Redirect(routes.ThinkingSessions.index(thinkingSessionId))
      }
      case None => {
        Logger.debug("No user cookie, bad request")
        BadRequest
      }
    }
  }

  /**
   * Handles adding cards for a remote procedure call
   */
  def addCardRPC(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User) = {
    Logger.debug("Cards.addCard")
    val cardId = Card.create(content, thinkingSession, hat, creator, 0, 0, None, None)
    Logger.debug("Creating card thru RPC with id: " + cardId)
    Redirect(routes.ThinkingSessions.index(thinkingSession.id))
  }

  /**
   * TODO: retrieve card specified by session id and card id from db and update id. Redirect to session index
   * Needed Info from HTML Params:
   * - newContent: String
   */
  def editCard(sessionId: Long, cardId: Long) = TODO

  /**
   * TODO: delete Card specified by the ids, redirect to session index
   */
  def deleteCard(sessionId: Long, cardId: Long) = TODO

  def restFormAddCard(sessionId: Long) = Action { implicit request =>
    Logger.debug("Cards.restFormAddCards(" + sessionId + ")")
    val formCard = cardForm.bindFromRequest.get
    request.cookies.get(User.idCookie) match {
      case Some(cookie) => {
        val user = User.byCookie(cookie).get;
        val hat = Hat.byName(formCard.hat);
        val cardId = Card.create(formCard.content, sessionId, hat.id, user.id, 0, 0, None, None);
        Ok(Json.obj("id" -> cardId,
          "hat" -> hat.name.toLowerCase,
          "content" -> formCard.content,
          "username" -> user.name)).as("application/json")
      }
      case None => {
        Logger.debug("No user cookie, bad request")
        BadRequest(Json.obj("error" -> "no user")).as("application/json")
      }
    }
  }
  def restJsonAddCard(sessionId: Long, hatId: Long) = Action(parse.json) { implicit request =>
    Logger.debug("Cards.restAddCard")
    val formCard = cardForm.bindFromRequest.get
    (request.body) match {
      case body: JsObject =>
        body \ "name" match {
          case JsString(name) =>
            if (name == "content") {
              request.body \ "value" match {
                case JsString(content) =>
                  request.cookies.get(User.idCookie) match {
                    case Some(cookie) => {
                      val user = User.byCookie(cookie).get;
                      val hat = Hat.byId(hatId)
                      val cardId = Card.create(content, sessionId, hatId, user.id, 0, 0, None, None)

                      val json = Json.obj(
                        "status" -> 200,
                        "fn" -> "createCard",
                        "args" -> Json.obj(
                          "id" -> cardId,
                          "hat" -> hat.name,
                          "content" -> content,
                          "user" -> user.name))
                      Ok(Json.obj("content" -> json)).as("application/json")

                    }
                    case None => {
                      Logger.debug("No user cookie, bad request")
                      BadRequest(Json.obj("error" -> true,
                        "message" -> "Could not find user cookie")).as("application/json")
                    }
                  }

                case _ => BadRequest(Json.obj("error" -> true,
                  "message" -> "Could not find JSOn elem \"value\" =(")).as("application/json")
              }
            } else {
              BadRequest(Json.obj("error" -> true,
                "message" -> "\"name\" was not content =(")).as("application/json")
            }
          case _ =>
            BadRequest(Json.obj("error" -> true,
              "message" -> "Could not find JSON elem \"name\" =(")).as("application/json")
        }
      case _ =>
        BadRequest(Json.obj("error" -> true,
          "message" -> "Request body not a JSON object =p")).as("application/json")

    }
  }

}