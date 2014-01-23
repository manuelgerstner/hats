package controllers;

import java.io.IOException;

import models.Hat;
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
		JsonNode jsonCard = new ObjectMapper().readTree(args[0].asText());

		// message to be added as a Card
		String content = jsonCard.get("content").asText();

		// hat color
		String hatAsString = jsonCard.get("hat").asText();
		Hat hat = Hat.byName(hatAsString);

		// user name DUMMY TODO
		String userAsString = jsonCard.get("user").asText();
		User user = User.dummy();

		ThinkingSession tSession = ThinkingSession.dummy();

		controllers.Cards.addCardRPC(content, tSession, hat, user);

		WAMPlayServer.publish(jsonCard.get("thinkingSession").asText(),
				jsonCard);
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