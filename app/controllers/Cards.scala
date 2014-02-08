package controllers

import models._

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

  def createBucket(id: Long) = Action { implicit request =>
    request.cookies.get(User.idCookie) match {
      case Some(cookie) =>
        User.byCookie(cookie) match {
          case Some(user) =>
            ThinkingSession.byId(id) match {
              case Some(session) =>
                if (ThinkingSession.checkUser(session, user)) {
                  val bucketId = Bucket.create(session)
                  Bucket.byId(bucketId) match {
                    case Some(bucket) =>
                      // create event
                      Ok(bucket.asJson).as("application/json")
                    case None => BadRequest
                  }
                } else BadRequest
              case None => NotFound
            }
          case None => BadRequest
        }
      case None => BadRequest
    }
  }

  def addCardToBucket(bucketId: Long, cardId: Long) = TODO

  // no return necessary
  def renameBucket(bucketId: Long) = Action { implicit request =>
    //    val name = (request.body.asJson \ "name").as[]
    //    Bucket.saveName(name, bucketId)
    Ok
  }

}
