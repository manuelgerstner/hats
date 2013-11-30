package controllers

import play.api._
import play.api.mvc.Controller
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import controllers._
import models._
import models.forms._

/**
 * Card Controller responsible for handling CRUD operations on ideas/cards (will be treated as synonyms,
 * as a card is merely the visual representation of an idea)
 *
 * @author: NemoOudeis
 */
object Cards extends Controller {

  /**
   * Needed Info from HTML Params or (browser)session/user info:
   * - HatID : Long  		Dummy for now
   * - UserID : Long		Dummy for now
   * - content : String
   * Content needs to be a string obviously.
   * To use this endpoint see CardForm (below)
   */
  def addCard(thinkingSessionId: Long) = Action { implicit request =>
    val formCard = cardForm.bindFromRequest.get
    Card.createFromFormCard(formCard, ThinkingSession.getById(thinkingSessionId), Hat.getDummyHat, User.getDummyUser1)

    Redirect(routes.ThinkingSessions.index(thinkingSessionId))
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
  def deleteCard(id: Long, cardId: Long) = TODO

  /**
   * Form to create a card. To use in HTML template do this you'll need to pass it as a render arg
   * and put the form somewhere like this:
   *
   * @import help._
   *
   * ...
   *
   * @helper.form(action = routes.Cards.addCard(thinkingSession.id)) {
   * @helper.inputText(cardForm("content"))
   * }
   *
   */
  val cardForm = Form(
    mapping(
      "content" -> nonEmptyText)(FormCard.apply)(FormCard.unapply))

}