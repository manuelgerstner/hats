package controllers;

import java.io.IOException;

import models.Hat;
import models.HatFlow;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

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
		User user = User.dummy();
		ThinkingSession tSession = ThinkingSession.byId(eventData.get("thinkingSession").asLong()).get();
		controllers.Cards.addCardRPC(eventData.get("content").asText(), tSession, hat, user);
		WAMPlayServer.publish(eventData.get("thinkingSession").asText(),
				jsonResponse);
	}

	@onRPC("#deleteCard")
	public static void deleteCard(String sessionId, JsonNode[] args) {
		// TODO
	}

	@onRPC("#moveHat")
	public static void moveHat(String sessionId, JsonNode[] args) throws JsonProcessingException, IOException {
		JsonNode jsonResponse = new ObjectMapper().readTree(args[0].asText());
		
		// event data containing current hat and session id
		JsonNode eventData = jsonResponse.get("eventData");
		
		long tSessionId = eventData.get("thinkingSession").asLong();
		
		long nextHatId = HatFlow.nextDefaultHatId(ThinkingSession.byId(tSessionId).get());
		Hat nextHat = Hat.byId(nextHatId);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode pubResponse = mapper.readTree("{ \"eventType\" : \"moveHat\" ,\"eventData\": {\"hat\":\"" + nextHat.name() + "\" }}");
		System.out.println(pubResponse.asText());		
		ThinkingSessions.changeHat(tSessionId);
		WAMPlayServer.publish(String.valueOf(tSessionId),
				pubResponse);
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