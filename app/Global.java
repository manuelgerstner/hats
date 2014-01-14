import play.Application;
import play.GlobalSettings;
import wamplay.controllers.WAMPlayServer;
import controllers.WebSocket;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		WAMPlayServer.addController(new WebSocket());
	}
}