import models.ThinkingSession
import play.api._
import ws.wamplay.controllers.WAMPlayServer
import controllers.WebSocket

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    WAMPlayServer.addController(new WebSocket());
    // add all topics already in the database upon app start
    val allSessions = ThinkingSession.all();
    for (session <- ThinkingSession.all()) {
      WAMPlayServer.addTopic(String.valueOf(session.id));
    }
  }
}