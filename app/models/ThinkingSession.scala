package models

import scala.concurrent.Future

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._
import play.api.mvc.AsyncResult

/**
 * The ThinkingSession models a whole session of the process. Don't read the code, read the comments ;-D
 * @author Nemo
 */
case class ThinkingSession(id: Long, owner: User, title: String, currentHat: Hat)

object ThinkingSession {

  /**
   * id                    	integer NOT NULL DEFAULT nextval('thinking_session_id_seq') PRIMARY KEY,
   * owner					integer NOT NULL REFERENCES `user`(id),
   * title                 	varchar(255) NOT NULL,
   * current_hat			integer REFERENCES hat(id)
   */
  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("id") ~
      get[Long]("owner") ~
      get[String]("title") ~
      get[Long]("current_hat") map {
        case id ~ ownerId ~ title ~ hatId => ThinkingSession(id, User.getById(ownerId), title, Hat.getById(hatId));
      }
  }

  /**
   * Retrieve all Thinking Sessions (not sure if we'll ever need this...)
   */
  def all(): List[ThinkingSession] = {
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
    getSessionsByOwner(owner.id)
  }

  def getSessionsByOwner(ownerId: Long): List[ThinkingSession] = {
    DB.withConnection { implicit connection =>
      SQL("select * from thinking_session where owner = {ownerId}").on(
        'ownerId -> ownerId).as(ThinkingSession.DBParser *)
    }
  }

  /**
   * Create a new ThinkingSession. Only needed argument is the current hat (i.e. starting hat for a new session).
   * The sessions cards are not needed, because the Card Object/Table holds a foreign key to the
   * parent sessions and there is no nicer way to model the 1:n relations ship with anorm (afaik)
   * This will NOT return the created Thinking Session!
   */
  def create(owner: User, title: String, hat: Hat): Int = {
    create(owner.id, title, hat.id)
  }

  def create(ownerId: Long, title: String, hatId: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("insert into thinking_session (owner,title,current_hat) values ({ownerId},{title},{hatId})").on(
        'ownerId -> ownerId,
        'title -> title,
        'hatId -> hatId).executeUpdate()
    }
  }

  /**
   * Changes the current Hat of a thinking session in db. Caution: the input model obj will still have the
   * old hat state (as it is assumed to be an immutable obj). For further use requery the ThinkingSession
   * from db (e.g. by ThinkginSession.getById([session id]) )
   */
  def changeHatTo(session: ThinkingSession, newHat: Hat): Int = {
    changeHatTo(session.id, newHat.id)
  }

  def changeHatTo(sessionId: Long, newHatId: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("""
          update thinking_session
          set current_hat = {newHatId}
          where id = {sessionId}
          """).on(
        'newHatId -> newHatId,
        'sessionId -> sessionId).executeUpdate()
    }
  }

  /**
   * Delete a Thinking Session
   */
  def delete(session: ThinkingSession): Int = {
    delete(session.id)
  }

  def delete(sessionId: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("delete from card where id = {id}").on(
        'id -> sessionId).executeUpdate()
    }
  }

  /**
   * Dummy Session for dev purposes
   */
  def dummy: ThinkingSession = {
    (all() head)
  }

  def dummyId: Long = 1
}
