package controllers;

import java.io.IOException;
import java.util.Date;

import models.Event;
import models.Hat;
import models.HatFlow;
import models.ThinkingSession;
import models.User;
import play.Logger;
import play.libs.Json;
import scala.Option;
import ws.wamplay.annotations.URIPrefix;
import ws.wamplay.annotations.onPublish;
import ws.wamplay.annotations.onRPC;
import ws.wamplay.annotations.onSubscribe;
import ws.wamplay.controllers.WAMPlayContoller;
import ws.wamplay.controllers.WAMPlayServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@URIPrefix("http://sixhats.com/cards")
public class WebSocket extends WAMPlayContoller {
	// make scala option's None available in Java
	private static Option<Object> none = scala.Option.apply(null);

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

	@onRPC("#moveHat")
	public static void moveHat(String sessionId, JsonNode[] args)
			throws JsonProcessingException, IOException {

		JsonNode data = new ObjectMapper().readTree(args[0].asText());

		long thinkingSessionId = data.get("eventData").get("thinkingSession")
				.asLong();
		long nextHatId = HatFlow.nextDefaultHatId(ThinkingSession.byId(
				thinkingSessionId).get());
		// final Hat nextHat = Hat.byId(nextHatId).get();

		ThinkingSession.changeHatTo(thinkingSessionId, nextHatId);

		long eventId = Event.create("moveHat", thinkingSessionId, nextHatId,
				none, none, new Date());
		Option<Event> event = Event.byId(eventId);
		JsonNode response = null;
		if (event.isDefined()) {
			response = event.get().toJson();
		} else {
			response = Json.newObject();
			((ObjectNode) response).put("error", "500");
		}

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