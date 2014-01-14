package controllers;

import play.libs.Json;
import wamplay.annotations.URIPrefix;
import wamplay.annotations.onPublish;
import wamplay.annotations.onRPC;
import wamplay.annotations.onSubscribe;
import wamplay.controllers.WAMPlayContoller;

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

	@onSubscribe("chat")
	public static boolean capitalSubscribe(String sessionID) {
		return true;
	}

	@onPublish("chat")
	public static JsonNode truncatePublish(String sessionID, JsonNode event) {
		return Json.toJson(event);
	}

}