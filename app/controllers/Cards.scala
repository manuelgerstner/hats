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
   * Handles adding cards for a remote procedure call
   */
  def addCardRPC(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User) = {
    Logger.debug("Cards.addCard")
    val cardId = Card.create(content, thinkingSession, hat, creator, 0, 0, None, None)
    Logger.debug("Creating card thru RPC with id: " + cardId)
    //    Redirect(routes.ThinkingSessions.index(thinkingSession.id))
  }
  def createBucket(sessionId: Long) = TODO

  def addCardToBucket(bucketId: Long, cardId: Long) = TODO

  // no return necessary
  def renameBucket(bucketId: Long) = TODO

}