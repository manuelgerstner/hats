package models

import anorm._
import anorm.SqlParser._
import models._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import akka.util.HashCode
import forms.FormCard
import scala.language.postfixOps

/**
 * Cards models an idea created by a ThinkingSession participant in the course of the 6 Hats process.
 * Every cards holds information about the user (creator), the hat in which it was created, the ThinkingSession
 * it belongs to and of course the content (for now only text)
 * @author Nemo
 */
case class Card(id: Long, thinkingSession: ThinkingSession, content: String, hat: Hat, creator: User)

object Card {

  def dummy: Card = {
    Card(1, ThinkingSession.dummy, "dfsfafsd", Hat.dummy, User.dummy);
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
        case id ~ thinkingSessionId ~ content ~ hatId ~ creatorId =>
          Card(id, ThinkingSession.byId(thinkingSessionId), content, Hat.byId(hatId),
            User.byId(creatorId) match { case Some(user) => user case None => null });
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
  def byThinkingSession(thinkingSession: ThinkingSession): List[Card] = {
    byThinkingSession(thinkingSession.id)
  }

  def byThinkingSession(thinkingSessionId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where thinking_session={id}").on(
        'id -> thinkingSessionId).as(Card.DBParser *)
    }
  }

  /**
   * Get all cards created by a user
   */
  def byUser(user: User): List[Card] = {
    byUser(user.id)
  }

  def byUser(userId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {id}").on(
        'id -> userId).as(Card.DBParser *)
    }
  }

  /**
   * Get all cards created by a user in a specific session
   */
  def byUserInSession(user: User, session: ThinkingSession): List[Card] = {
    byUserInSession(user.id, session.id)
  }

  def byUserInSession(userId: Long, sessionId: Long): List[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where creator = {userId} and thinking_session ={sessionId}").on(
        'userId -> userId,
        'sessionId -> sessionId).as(Card.DBParser *)
    }
  }

  /**
   * Create a new card.
   * This will return the id of the created card
   */
  def create(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User): Int = {
    create(content, thinkingSession.id, hat.id, creator.id)
  }

  def nextId(): Long = {
    DB.withConnection { implicit connection =>
      SQL("SELECT CARD_ID_SEQ.nextval;").apply().map {
        case Row(name: String, value: Int) => Int.int2long(value)
      }.head
    }
  }

  /**
   * Create a new card.
   * This will return the id of the created card
   */
  def create(content: String, thinkingSessionId: Long, hatId: Long, creatorId: Long): Int = {
    DB.withConnection { implicit connection =>
      // create unique id by hashing contents + timestamp
      val id: Int = (content + thinkingSessionId + hatId + creatorId + System.currentTimeMillis).hashCode
      SQL("insert into card (id,content,thinking_session,hat,creator) values ({id},{content},{thinkingSessionId},{hat},{creatorId})").on(
        'id -> id,
        'content -> content,
        'thinkingSessionId -> thinkingSessionId,
        'hat -> hatId,
        'creatorId -> creatorId).executeUpdate()
      id
    }
  }

  /**
   * Convenience function creating a new Card from a formCard with bound values from a HTML form and the
   * creating user.
   */
  def create(formCard: FormCard, thinkingSessionId: Long, userId: Long): Int = {
    create(formCard.content, thinkingSessionId, Hat.byName(formCard.hat).id, userId)
  }

  def create(formCard: FormCard, thinkingSession: ThinkingSession, user: User): Int = {
    create(formCard, thinkingSession.id, user.id)
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
    //{"id":5, "hat": "Green", "content": "card content", "user":"username"}
    Json.obj(
      "id" -> card.id,
      "hat" -> card.hat.name.toLowerCase,
      "thinkingSessionId" -> card.thinkingSession.id,
      "content" -> card.content,
      "username" -> card.creator.name)
  }

  def listToJson(cards: List[Card]) = {
    Json.toJson(cards.map(card => Card.toJson(card)))
  }
}