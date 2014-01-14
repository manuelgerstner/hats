package wamplay.controllers.messageHandlers;


import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.Logger.ALogger;
import wamplay.callbacks.PubSubCallback;
import wamplay.controllers.WAMPlayServer;
import wamplay.models.PubSub;
import wamplay.models.WAMPlayClient;


public class SubscribeHandler implements MessageHandler {
	static ALogger log = Logger.of(SubscribeHandler.class);

	@Override
	public void process(WAMPlayClient senderClient, JsonNode message) {
		String topic = message.get(1).asText();

		PubSubCallback cb = PubSub.getPubSubCallback(topic);

		if (cb == null) {
			log.error("Topic not found: " + topic);
			return;
		}

		boolean sucessful = cb.runSubCallback(senderClient.getSessionID());

		if (!sucessful) {
			log.debug("Callback for " + topic + " canceled.");
			return;
		}

		if (WAMPlayServer.isTopic(topic)) {
			senderClient.subscribe(topic);
			return;
		}
		log.error("Client tried to subscribe to nonexistant topic: " + topic);
	}

}
