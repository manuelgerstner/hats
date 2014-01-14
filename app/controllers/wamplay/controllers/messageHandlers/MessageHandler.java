package controllers.wamplay.controllers.messageHandlers;


import com.fasterxml.jackson.databind.JsonNode;

import controllers.wamplay.models.WAMPlayClient;


public interface MessageHandler {
	public void process(WAMPlayClient client, JsonNode message);
}
