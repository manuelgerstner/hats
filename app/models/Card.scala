package models

import anorm._
import anorm.SqlParser._

import models._
import forms._

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

/**
 * Cards models an idea created by a ThinkingSession participant in the course of the 6 Hats process.
 * Every cards holds information about the user (creator), the hat in which it was created, the ThinkingSession
 * it belongs to and of course the content (for now only text)
 * @author Nemo
 */
case class Card(id: Long, thinkingSession: ThinkingSession, content: String, hat: Hat, creator: User)

object Card {

  def getTest: Card = {
    Card(1, null, "dfsfafsd", null, null);
  }
  /**
   * ORM simple
   */
  val simple = {
    get[Long]("id") ~
      get[Long]("thinking_session_id") ~
      get[String]("content") ~
      get[Long]("hat") ~
      get[Long]("creator") map {
        case id ~ thinkingSessionId ~ content ~ hatId ~ creatorId => Card(id, ThinkingSession.getById(thinkingSessionId), content, Hat.getById(hatId), User.getById(creatorId));
      }
  }

  /**
   * Get all Cards (guess we'll never need this...)
   */
  def all(): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card").as(Card.simple *)
    }
  }

  /**
   * Get all cards of a given ThinkingSession
   */
  def getThinkingSessionCards(thinkingSessionId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where thinking_session_id = {id}").on(
        'id -> thinkingSessionId).as(Card.simple *)
    }
  }

  def getThinkingSessionCards(thinkingSession: ThinkingSession): List[Card] = {
    getThinkingSessionCards(thinkingSession.id)
  }

  /**
   * Get all cards created by a user
   */
  def getUserCards(user: User): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {id}").on(
        'id -> user.id).as(Card.simple *)
    }
  }

  /**
   * Get all cards created by a user in a specific session
   */
  def getUserCardsIndSession(user: User, session: ThinkingSession): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {userId} and thinking_session_id ={sessionId}").on(
        'userId -> user.id,
        'sessionId -> session.id).as(Card.simple *)
    }
  }

  /**
   * Create a new card. Every card needs:
   * - Content (text for now)
   * - belongs to a ThinkingSession
   * - created in a Hat
   * - created by a User
   * This will NOT return the created Card!
   */
  def create(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User) = {
    DB.withConnection { implicit connection =>
      SQL("insert into card (content,thinking_session_id,hat,creator) values ({content},{thinkingSessionId},{hat},{creatorId})").on(
        'content -> content,
        'thinkingSessionId -> thinkingSession.id,
        'hat -> hat.id,
        'creatorId -> creator.id).executeUpdate()
    }
  }

  /**
   * Convenience function creating a new Card from a foromCard with bound values from a HTML form and the
   * creating user.
   */
  def createFromFormCard(formCard: FormCard, thinkingSession: ThinkingSession, hat: Hat, user: User) = {
    create(formCard.content, thinkingSession, hat, user)
  }

  /**
   * Delete a card (if it was shit or something...)
   */
  def delete(card: Card) = {
    DB.withConnection { implicit connection =>
      SQL("delete from card where id = {id}").on(
        'id -> card.id).executeUpdate()
    }
  }

  def toJson(card: Card): JsObject = {
    //case class Card(id: Long, thinkingSessionId: Long, content: String, hat: Hat, creator: User)
    Json.obj(
      "id" -> card.id,
      "thinkingSessionId" -> card.thinkingSession.id,
      "content" -> card.content
    //      card.hat

    )
  }

}
