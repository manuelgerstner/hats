package controllers;

import java.io.IOException;

import models.Hat;
import models.ThinkingSession;
import models.User;
import play.Logger;
import play.libs.Json;
import scala.Option;
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

		// user name DUMMY TODO
		User user = User.byId(Long.getLong(sessionId)).get();

		ThinkingSession tSession = ThinkingSession.byId(Long.getLong(eventData.get("thinkingSession").asText())).get();
		controllers.Cards.addCardRPC(eventData.get("content").asText(), tSession, hat, user);
		WAMPlayServer.publish(eventData.get("thinkingSession").asText(),
				jsonResponse);
	}

	@onRPC("#deleteCard")
	public static void deleteCard(String sessionId, JsonNode[] args) {
		// TODO
	}

	@onRPC("#moveHat")
	public static void moveHat(String sessionId) {

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