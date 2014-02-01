package models

import anorm._
import anorm.SqlParser._
import models._
import models.forms._
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
case class Card(
  id: Long,
  thinkingSession: ThinkingSession,
  content: String,
  hat: Hat,
  creator: User,
  posX: Int,
  posY: Int,
  imgUrl: Option[String],
  imgMime: Option[String]) {

  def toJson(): JsObject = {
    Json.obj(
      "id" -> this.id,
      "hat" -> this.hat.name.toLowerCase,
      "thinkingSessionId" -> this.thinkingSession.id,
      "content" -> this.content,
      "username" -> this.creator.name,
      "posX" -> this.posX,
      "posY" -> this.posY,
      "imgUrl" -> this.imgUrl,
      "imgMime" -> this.imgMime)
  }
}

object Card {

  val dummy: Card = Card(1, ThinkingSession.dummy, "dfsfafsd", Hat.dummy, User.dummy, 0, 0, None, None);

  /**
   * ORM simple
   */
  //as(get[Option[String]]("something") ?).getOrElse(None)
  val DBParser = {
    get[Long]("id") ~
      get[Long]("thinking_session") ~
      get[String]("content") ~
      get[Long]("hat") ~
      get[Long]("creator") ~
      get[Int]("pos_x") ~
      get[Int]("pos_y") ~
      (get[String]("img_url") ?) ~
      (get[String]("img_mime") ?) map {
        case id ~ thinkingSessionId ~ content ~ hatId ~ creatorId ~ posX ~ posY ~ imgUrl ~ imgMime =>
          Card(id,
            ThinkingSession.byId(thinkingSessionId).get, content, Hat.byId(hatId).get,
            User.byId(creatorId).get, posX, posY, imgUrl, imgMime);
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

  def byId(id: Long): Option[Card] = {
    DB.withConnection { implicit connection =>
      SQL("select * from card where id = {id}").on(
        'id -> id).as(Card.DBParser *).headOption
    }
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
  def create(content: String, thinkingSessionId: Long, hatId: Long, creatorId: Long,
    posX: Int, posY: Int, imgUrl: Option[String], imgMime: Option[String]): Int = {
    DB.withConnection { implicit connection =>
      // create unique id by hashing contents + timestamp
      val id: Int = (content + thinkingSessionId + hatId + creatorId + System.currentTimeMillis).hashCode
      SQL("""
          insert into card (id,content,thinking_session,hat,creator,pos_x,pos_y,img_url,img_mime) 
          values ({id},{content},{thinkingSessionId},{hat},{creatorId},{posX},{posY},{imgUrl},{imgMime})
          """).on(
        'id -> id,
        'content -> content,
        'thinkingSessionId -> thinkingSessionId,
        'hat -> hatId,
        'creatorId -> creatorId,
        'posX -> posX,
        'posY -> posY,
        'imgUrl -> imgUrl,
        'imgMime -> imgMime).executeUpdate()
      id
    }
  }

  /**
   * Convenience function creating a new Card from a formCard with bound values from a HTML form and the
   * creating user.
   */
  def create(formCard: FormCard, thinkingSessionId: Long, userId: Long, posX: Int, posY: Int, imgUrl: Option[String], imgMime: Option[String]): Int = {
    create(formCard.content, thinkingSessionId, Hat.byName(formCard.hat).id, userId, posX, posY, imgUrl, imgMime)
  }

  def create(formCard: FormCard, thinkingSession: ThinkingSession, user: User, posX: Int, posY: Int, imgUrl: Option[String], imgMime: Option[String]): Int = {
    create(formCard, thinkingSession.id, user.id, posX, posY, imgUrl, imgMime)
  }

  /**
   * Create a new card.
   * This will return the id of the created card
   */
  def create(content: String, thinkingSession: ThinkingSession, hat: Hat, creator: User, posX: Int, posY: Int, imgUrl: Option[String], imgMime: Option[String]): Int = {
    create(content, thinkingSession.id, hat.id, creator.id, posX, posY, imgUrl, imgMime)
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

  def listToJson(cards: List[Card]) = {
    Json.toJson(cards.map(card => card.toJson()))
  }

  /**
   * Count number of cards per hat
   */
  def byHatNoCards: List[(String, Long)] = {
    DB.withConnection { implicit connection =>
      SQL("""select name , COUNT(card.id) as cNO from card, hat where card.hat = hat.id GROUP BY card.hat order by card.hat""")
        .as(str("name") ~ long("cNO") map { case name ~ cNO => (name, cNO) } *)
    }

  }
}