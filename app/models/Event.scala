package models;

import anorm._
import anorm.SqlParser._
import play.libs.Json
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.Date
import scala.collection.immutable.HashMap
import scala.collection.JavaConversions
import play.api.Play.current
import play.api.db.DB

case class Event(
  id: Long,
  eventType: String,
  thinkingSession: ThinkingSession,
  hat: Hat,
  user: Option[User],
  card: Option[Card],
  time: Date) {

  def toJson(): JsonNode = {
    if (eventType == null) {
      throw new IllegalArgumentException();
    }

    var result: ObjectNode = Json.newObject();
    result.put("eventType", this.eventType);
    result.put("eventData", Json.toJson(getEventData));
    return result;
  }

  def getEventData: java.util.Map[String, String] = {
    val baseData = HashMap[String, String](
      "thinkinSession" -> thinkingSession.id.toString,
      "hat" -> hat.name,
      "time" -> time.getTime().toString(),
      "user" -> user.collect({ case u: User => u.name }).toString(),
      "card" -> card.collect({ case c: Card => c.id }).toString())
    JavaConversions.mapAsJavaMap(baseData)
  }

  val hasCard: Boolean = card match {
    case Some(_) => true
    case None => false
  }

  val hasUser: Boolean = user match {
    case Some(_) => true
    case None => false
  }
}

object Event {
  val dummy = Event(0, "dummyEvent", ThinkingSession.dummy, Hat.dummy, None, None, new Date())

  val DBParser = {
    get[Long]("id") ~
      get[String]("type") ~
      get[Long]("thinking_session") ~
      get[Long]("hat") ~
      (get[Long]("user") ?) ~
      (get[Long]("card") ?) ~
      get[Date]("time") map {
        case id ~ eventType ~ thinkingSessionId ~ hatId ~ userId ~ cardId ~ time =>
          Event(id, eventType,
            ThinkingSession.byId(thinkingSessionId).get, Hat.byId(hatId).get,
            userId match { case Some(u) => User.byId(u) case None => None },
            cardId match { case Some(c) => Card.byId(c) case None => None }, time);
      }
  }

  def create(eventType: String, thinkingSessionId: Long, hatId: Long, userId: Option[Long], cardId: Option[Long], time: Date): Long = {
    DB.withConnection { implicit connection =>
      val id: Long = (eventType.hashCode() + thinkingSessionId + userId.hashCode() + cardId.hashCode() + time.getTime()).hashCode()
      val sql = SQL("""
          insert into event (id,type,thinking_session,hat,user,card,time) 
          values ({id},{eventType},{thinkingSessionId},{hatId},{userId},{cardId},{time})
          """).on(
        'id -> id,
        'eventType -> eventType,
        'thinkingSessionId -> thinkingSessionId,
        'hatId -> hatId,
        'userId -> userId,
        'cardId -> cardId,
        'time -> time)

      sql.executeUpdate()
      id
    }
  }

  def create(eventType: String, thinkingSession: ThinkingSession, hat: Hat, user: Option[User], card: Option[Card], time: Date): Long = {
    create(eventType, thinkingSession.id, hat.id, user.collect({ case u: User => u.id }),
      card.collect({ case c: Card => c.id }), time)
  }

  def byId(id: Long): Option[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from event where id = {id}").on(
        'id -> id).as(DBParser *).headOption
    }
  }

  def byThinkingSession(thinkingSession: ThinkingSession): List[Event] = {
    byThinkingSession(thinkingSession.id)
  }

  def byThinkingSession(thinkingSessionId: Long): List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from event where thinking_session={id}").on(
        'id -> thinkingSessionId).as(DBParser *)
    }
  }

  def byUser(user: User): List[Event] = {
    byUser(user.id)
  }

  def byUser(userId: Long): List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from event where creator = {id}").on(
        'id -> userId).as(DBParser *)
    }
  }
}
