package models

import play.api.db.DB
import play.api.db._

import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * Simple model obj for work flow storage (i.e. sequence of hats)
 * @author Nemo
 */
case class HatFlow(index: Long, thinkingSession: Long, hat: Long)

object HatFlow {

  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("index") ~
      get[Long]("thinking_session") ~
      get[Long]("hat") map {
        case index ~ sessionId ~ hatId => HatFlow(index, sessionId, hatId);
      }
  }

  /**
   * Retrieve the HatFlow list for the specified session obj from db
   */
  def getHatFlow(session: ThinkingSession): List[HatFlow] = {
    getHatFlow(session.id)
  }

  /**
   * Retrieve the HatFlow list for the specified session from db
   */
  def getHatFlow(sessionId: Long): List[HatFlow] = {
    DB.withConnection { implicit connection =>
      SQL("select * from hat_flow where thinking_session={sessionId}").on(
        'sessionId -> sessionId).as(HatFlow.DBParser *)
    }
  }

  /**
   * Get the next hat id for the input ThinkingSesssion obj
   */
  def getNextHatId(session: ThinkingSession): Long = {
    getNextHatId(session.id, session.currentHat.id)
  }

  /**
   * get the next hat of the session given a current hat
   */
  def getNextHatId(sessionId: Long, currentHatId: Long): Long = {
    getNextHatId(getHatFlow(sessionId), currentHatId)
  }
  /**
   * given a list of HatFlow and the current hat this will return the id of the next Hat in the workflow
   */
  def getNextHatId(flow: List[HatFlow], currentHatId: Long): Long = {
    val currentHat = (flow filter ((hatFlow: HatFlow) => hatFlow.hat == currentHatId)).head
    (flow filter ((hatFlow: HatFlow) => hatFlow.index == ((currentHat.index + 1) % (flow size)))).head.hat
  }

  /**
   * dummy hat flow for dev purposes. it goes like this:
   * White -> Green -> Yellow -> Red -> Black -> Blue
   */
  def defaultHatFlow: List[HatFlow] = getHatFlow(1)

  /**
   * for a given session the next hat id according to the dummy workflow
   */
  def getNextDefaultHatId(session: ThinkingSession): Long = {
    getNextHatId(defaultHatFlow, session.currentHat.id)
  }

}
