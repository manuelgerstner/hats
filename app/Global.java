import play.Application;
import play.GlobalSettings;
import controllers.WebSocket;
import controllers.wamplay.controllers.WAMPlayServer;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		WAMPlayServer.addTopic("thinkingSession/cards");
		WAMPlayServer.addTopic("hatsSession_1");
		WAMPlayServer.addController(new WebSocket());
	}
}
