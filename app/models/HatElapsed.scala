package models

import play.api.db.DB
import play.api.db._

import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
 * Elapsed Time for each Hat on Current Session
 * @author Anamika
 */
case class HatElapsed(thinking_session: Long, hat_id: String, elapsed_time: Long)

object HatElapsed {

  /**
   * ORM simple
   */
  val DBParser = {
    get[Long]("thinking_session") ~
      get[String]("hat_id") ~
      get[Long]("elapsed_time") map {
        case sessionId ~ hatId ~ elapsedTime => HatElapsed(sessionId, hatId, elapsedTime);
      }
  }
  /**
   * Count number of cards per hat
   */
  def byHatNoCards(id: Long): List[(String, Long)] = {
    DB.withConnection { implicit connection =>
      SQL("""select name , COUNT(card.id) as cNO from card, hat where card.hat = hat.id and thinking_session= {id} GROUP BY card.hat order by card.hat""")
        .on('id -> id)
        .as(str("name") ~ long("cNO") map { case name ~ cNO => (name, cNO) } *)
    }

  }
  /**
   * Count number of cards per hat
   */
  def byHatNoCardsPrev(id: Long): List[(String, Long)] = {
    var prevId: Long = id - 1;
    DB.withConnection { implicit connection =>
      SQL("""select name , COUNT(card.id) as cNO from card, hat where card.hat = hat.id and thinking_session= {prevId} GROUP BY card.hat order by card.hat""")
        .on('prevId -> prevId)
        .as(str("name") ~ long("cNO") map { case name ~ cNO => (name, cNO) } *)
    }

  }
  /**
   * Count number of cards per hat
   */
  def byHatNoCardsPrev2(id: Long): List[(String, Long)] = {
    var prevId: Long = id - 2;
    DB.withConnection { implicit connection =>
      SQL("""select name , COUNT(card.id) as cNO from card, hat where card.hat = hat.id and thinking_session= {prevId} GROUP BY card.hat order by card.hat""")
        .on('prevId -> prevId)
        .as(str("name") ~ long("cNO") map { case name ~ cNO => (name, cNO) } *)
    }

  }

  /**
   * Insert in hat_elapsed table
   * Thinking session Id
   * Hat name
   * Elapsed time
   */
  def insertElapsedTime(id: Long, hat_name: String, elapsedTime: Long) = {
    DB.withConnection { implicit connection =>
      SQL("""
          insert into hat_elapsed (thinking_session,hat_id,elapsed_time) 
          values ({id},{hat_name},{elapsedTime})
          """).on(
        'id -> id,
        'hat_name -> hat_name,
        'elapsedTime -> elapsedTime).executeUpdate()
    }
  }
  def hatTimeForCurrentSession(id: Long): (List[HatElapsed]) = {
    DB.withConnection { implicit connection =>
      SQL("select * from hat_elapsed where thinking_session = {id}").on(
        'id -> id).as(HatElapsed.DBParser *)
    }

  }

}
