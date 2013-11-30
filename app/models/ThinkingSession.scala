package models

import play.api.db.DB
import play.api.db._

import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * The ThinkingSession models a whole session of the process.
 * @author Nemo
 */
case class ThinkingSession(id: Long, owner: User, cards: List[Card], currentHat: Hat)

object ThinkingSession {

  /**
   * ORM simple
   */
  val simple = {
    get[Long]("id") ~
      get[Long]("owner") ~
      get[Long]("current_hat") ~
      get[String]("content") map {
        case id ~ ownerId ~ hatId ~ content => ThinkingSession(id, User.getById(ownerId), Card.getThinkingSessionCards(id), Hat.getById(hatId));
      }
  }

  /**
   * Retrieve all Thinking Sessions (not sure if we'll ever need this...)
   */
  def all(): List[ThinkingSession] = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session").as(ThinkingSession.simple *)
    }
  }

  /**
   * Load a specific Thinking Session from DB specified by given ID
   */
  def getById(id: Long): ThinkingSession = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session where id = {id}").on(
        'id -> id
      ).as(ThinkingSession.simple *) head
    }
  }

  /**
   * Retrieve all sessions a specified user is owner of
   */
  def getSessionsByOwner(owner: User): List[ThinkingSession] = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session where owner = {ownerId}").on(
        'ownerId -> owner.id
      ).as(ThinkingSession.simple *)
    }
  }

  /**
   * Create a new ThinkingSession. Only needed argument is the current hat (i.e. starting hat for a new session).
   * The sessions cards are not needed, because the Card Object/Table holds a foreign key to the
   * parent sessions and there is no nicer way to model the 1:n relations ship with anorm (afaik)
   * This will NOT return the created Thinking Session!
   */
  def create(owner: User, hat: Hat) = {
    DB.withConnection { implicit connection =>
      SQL("insert into thinking_Session (owner,current_hat) values ({ownerId},{hatId})").on(
        'ownerId -> owner.id,
        'hatId -> hat.id
      ).executeUpdate()
    }
  }

  /**
   * Delete a Thinking Session
   */
  def delete(session: ThinkingSession) = {
    DB.withConnection { implicit connection =>
      SQL("delete from card where id = {id}").on(
        'id -> session.id
      ).executeUpdate()
    }
  }

  /**
   * Dummy Session for dev purposes
   */
  def getDummer(): ThinkingSession = {
    all() head
  }
}
