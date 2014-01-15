package wamplay.controllers.messageHandlers;


import wamplay.models.WAMPlayClient;

import com.fasterxml.jackson.databind.JsonNode;


public class UnsubscribeHandler implements MessageHandler {

	@Override
	public void process(WAMPlayClient client, JsonNode message) {
		String topic = message.get(1).asText();
		client.unsubscribe(topic);
	}

}
