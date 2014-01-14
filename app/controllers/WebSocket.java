package controllers;

import play.Logger;
import play.libs.Json;
import wamplay.annotations.URIPrefix;
import wamplay.annotations.onPublish;
import wamplay.annotations.onRPC;
import wamplay.annotations.onSubscribe;
import wamplay.controllers.WAMPlayContoller;
import wamplay.controllers.WAMPlayServer;

import com.fasterxml.jackson.databind.JsonNode;

@URIPrefix("http://example.com/")
public class WebSocket extends WAMPlayContoller {

	@onRPC("#meaningOfLife")
	public static String getMeaningOfLife(String sessionID) {
		return "Meaning of life is: 42";
	}

	@onRPC("#capital")
	public static String add(String sessionID, JsonNode[] args) {
		String ans = args[0].asText().toUpperCase();
		return ans;
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