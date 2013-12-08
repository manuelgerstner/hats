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
   * id                    	integer NOT NULL DEFAULT nextval('card_id_seq') PRIMARY KEY,
   * thinking_session       integer NOT NULL REFERENCES thinking_session(id),
   * content               	text NOT NULL,
   * hat					integer REFERENCES hat(id),
   * creator		      	integer REFERENCES `user`(id)
   */
  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("id") ~
      get[Long]("thinking_session") ~
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
      SQL("select * from card").as(Card.DBParser *)
    }
  }

  /**
   * Get all cards of a given ThinkingSession
   */
  def getThinkingSessionCards(thinkingSession: ThinkingSession): List[Card] = {
    getThinkingSessionCards(thinkingSession.id)
  }

  def getThinkingSessionCards(thinkingSessionId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where thinking_session={id}").on(
        'id -> thinkingSessionId).as(Card.DBParser *)
    }
  }

  /**
   * Get all cards created by a user
   */
  def getUserCards(user: User): List[Card] = {
    getUserCards(user.id)
  }

  def getUserCards(userId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {id}").on(
        'id -> userId).as(Card.DBParser *)
    }
  }

  /**
   * Get all cards created by a user in a specific session
   */
  def getUserCardsInSession(user: User, session: ThinkingSession): List[Card] = {
    getUserCardsInSession(user.id, session.id)
  }

  def getUserCardsInSession(userId: Long, sessionId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {userId} and thinking_session ={sessionId}").on(
        'userId -> userId,
        'sessionId -> sessionId).as(Card.DBParser *)
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
  def create(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User): Int = {
    create(content, thinkingSession.id, hat.id, creator.id)
  }

  def create(content: String, thinkingSessionId: Long, hatId: Long, creatorId: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("insert into card (content,thinking_session,hat,creator) values ({content},{thinkingSessionId},{hat},{creatorId})").on(
        'content -> content,
        'thinkingSessionId -> thinkingSessionId,
        'hat -> hatId,
        'creatorId -> creatorId).executeUpdate()
    }
  }

  /**
   * Convenience function creating a new Card from a formCard with bound values from a HTML form and the
   * creating user.
   */
  def createFromFormCard(formCard: FormCard, thinkingSession: ThinkingSession, hat: Hat, user: User) = {
    create(formCard.content, thinkingSession, hat, user)
  }

  /**
   * Delete a card (if it was shit or something...)
   */
  def delete(card: Card): Int = {
    delete(card.id)
  }

  def delete(cardId: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("delete from card where id = {id}").on(
        'id -> cardId).executeUpdate()
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
