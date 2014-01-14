package controllers.wamplay.controllers.messageHandlers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import controllers.wamplay.models.WAMPlayClient;
import controllers.wamplay.models.messages.Welcome;


public class ConnectHandler implements MessageHandler {
	@Override
	public void process(WAMPlayClient client, JsonNode message) {
		List<Object> welcome = new Welcome(client.getSessionID()).toList();
		client.send(Json.toJson(welcome));
	}
}
