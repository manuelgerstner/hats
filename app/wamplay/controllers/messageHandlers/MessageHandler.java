package wamplay.controllers.messageHandlers;


import wamplay.models.WAMPlayClient;

import com.fasterxml.jackson.databind.JsonNode;


public interface MessageHandler {
	public void process(WAMPlayClient client, JsonNode message);
}
