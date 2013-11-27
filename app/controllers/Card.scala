package controllers

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._

/**
 * Card Controller responsible for handling CRUD operations on ideas/cards (will be treated as synonyms,
 * as a card is merely the visual representation of an idea)
 *
 * @author: NemoOudeis
 */
object Card extends Controller {

  /**
   * TODO: read card value from params, create new card in db and redirect to session index
   */
  def newCard(id: Long) = TODO

  /**
   * TODO: retrieve card specified by session id and card id from db and update id. Redirect to session index
   */
  def editCard(id: Long, cardId: Long) = TODO

  /**
   * TODO: delete Card specified by the ids, redirect to session index
   */
  def deleteCard(id: Long, cardId: Long) = TODO

  /**
   * Dummy Form to create a card
   */
  val cardForm = Form(
    "content" -> nonEmptyText
  )
}