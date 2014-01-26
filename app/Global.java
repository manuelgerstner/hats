import models.ThinkingSession;
import play.Application;
import play.GlobalSettings;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import wamplay.controllers.WAMPlayServer;
import controllers.WebSocket;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		WAMPlayServer.addController(new WebSocket());
		// add all topics already in the database upon app start
		List<ThinkingSession> allSessions = ThinkingSession.all();
		Iterator<ThinkingSession> i = allSessions.iterator();
		while (i.hasNext()) {
			WAMPlayServer.addTopic(String.valueOf(i.next().id()));
		}
	}
}