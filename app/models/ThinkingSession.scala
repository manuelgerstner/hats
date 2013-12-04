package models

import scala.concurrent.Future

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._
import play.api.mvc.AsyncResult

/**
 * The ThinkingSession models a whole session of the process.
 * @author Nemo
 */
case class ThinkingSession(id: Long, owner: User, title: String, cards: List[Card], currentHat: Hat)

object ThinkingSession {

  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("id") ~
      get[Long]("owner") ~
      get[String]("title") ~
      get[Long]("current_hat") map {
        case id ~ ownerId ~ title ~ hatId => ThinkingSession(id, User.getById(ownerId), title, Card.getThinkingSessionCards(id), Hat.getById(hatId));
      }
  }

  /**
   * Retrieve all Thinking Sessions (not sure if we'll ever need this...)
   */
  def all(): List[ThinkingSession] = {

    //    val conn = DB.getConnection()
    //    try {
    //      DB.withConnection { implicit conn =>
    //        SQL("select * from thinking_session").as(ThinkingSession.DBParser *)
    //      }
    //
    //    } finally {
    //      conn.close()
    //    }

    DB.withConnection { implicit conn =>
      SQL("select * from thinking_session").as(ThinkingSession.DBParser *)
    }

  }

  /**
   * Load a specific Thinking Session from DB specified by given ID
   */
  def getById(id: Long): ThinkingSession = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session where id = {id}").on(
        'id -> id).as(ThinkingSession.DBParser *) head
    }
  }

  /**
   * Retrieve all sessions a specified user is owner of
   */
  def getSessionsByOwner(owner: User): List[ThinkingSession] = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session where owner = {ownerId}").on(
        'ownerId -> owner.id).as(ThinkingSession.DBParser *)
    }
  }

  /**
   * Create a new ThinkingSession. Only needed argument is the current hat (i.e. starting hat for a new session).
   * The sessions cards are not needed, because the Card Object/Table holds a foreign key to the
   * parent sessions and there is no nicer way to model the 1:n relations ship with anorm (afaik)
   * This will NOT return the created Thinking Session!
   */
  def create(owner: User, title: String, hat: Hat) = {
    DB.withConnection { implicit connection =>
      SQL("insert into thinking_Session (owner,title,current_hat) values ({ownerId},{title},{hatId})").on(
        'ownerId -> owner.id,
        'title -> title,
        'hatId -> hat.id).executeUpdate()
    }
  }

  /**
   * Delete a Thinking Session
   */
  def delete(session: ThinkingSession) = {
    DB.withConnection { implicit connection =>
      SQL("delete from card where id = {id}").on(
        'id -> session.id).executeUpdate()
    }
  }

  /**
   * Dummy Session for dev purposes
   */
  def dummy: ThinkingSession = {
    (all() head)
  }
}
