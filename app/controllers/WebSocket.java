package controllers;

import java.io.IOException;
import java.util.HashMap;

import models.Event;
import models.Hat;
import models.HatFlow;
import models.ThinkingSession;
import models.User;
import play.Logger;
import play.libs.Json;
import wamplay.annotations.URIPrefix;
import wamplay.annotations.onPublish;
import wamplay.annotations.onRPC;
import wamplay.annotations.onSubscribe;
import wamplay.controllers.WAMPlayContoller;
import wamplay.controllers.WAMPlayServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@URIPrefix("http://sixhats.com/cards")
public class WebSocket extends WAMPlayContoller {

	// call addCard server-side
	@onRPC("#addCard")
	public static void add(String sessionId, JsonNode[] args)
			throws JsonProcessingException, IOException {
		JsonNode jsonResponse = new ObjectMapper().readTree(args[0].asText());

		// message to be added as a Card
		JsonNode eventData = jsonResponse.get("eventData");
		// hat color
		Hat hat = Hat.byName(eventData.get("hat").asText());

		long userId = eventData.get("userId").asLong();
		long thinkingSessionId = eventData.get("thinkingSession").asLong();

		if (User.byId(userId).isDefined()
				&& ThinkingSession.byId(thinkingSessionId).isDefined()) {
			User user = User.byId(userId).get();
			ThinkingSession tSession = ThinkingSession.byId(thinkingSessionId)
					.get();
			controllers.Cards.addCardRPC(eventData.get("content").asText(),
					tSession, hat, user);
			WAMPlayServer.publish(eventData.get("thinkingSession").asText(),
					jsonResponse);
		} else {
			throw new IOException();
		}

	}

	@onRPC("#deleteCard")
	public static void deleteCard(String sessionId, JsonNode[] args) {
		// TODO
	}

	@SuppressWarnings("serial")
	@onRPC("#moveHat")
	public static void moveHat(String sessionId, JsonNode[] args)
			throws JsonProcessingException, IOException {

		JsonNode data = new ObjectMapper().readTree(args[0].asText());

		long thinkingSessionId = data.get("eventData").get("thinkingSession")
				.asLong();
		long nextHatId = HatFlow.nextDefaultHatId(ThinkingSession.byId(
				thinkingSessionId).get());
		final Hat nextHat = Hat.byId(nextHatId);

		ThinkingSession.changeHatTo(thinkingSessionId, nextHatId);

		JsonNode response = new Event().setType("moveHat")
				.setData(new HashMap<String, String>() {
					{
						put("hat", nextHat.name());
					}
				}).toJson();

		WAMPlayServer.publish(String.valueOf(thinkingSessionId), response);
	}

	@onSubscribe
	public static boolean onSubscribe(String sessionID) {
		Logger.debug("subscribing session id" + sessionID);
		if (!WAMPlayServer.isTopic("thinkingSession_" + sessionID)) {
			WAMPlayServer.addTopic("thinkingSession_" + sessionID);
		}
		return true;
	}

	@onPublish
	public static JsonNode onPublish(String sessionID, JsonNode event) {

		return Json.toJson(event);
	}

}