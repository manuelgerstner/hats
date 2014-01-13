import play.Application;
import play.GlobalSettings;
import ws.wamplay.controllers.WAMPlayServer;
import controllers.SampleController;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		WAMPlayServer.addController(new SampleController());
	}

}