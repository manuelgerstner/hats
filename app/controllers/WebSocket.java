package controllers;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.wamplay.annotations.URIPrefix;
import controllers.wamplay.annotations.onPublish;
import controllers.wamplay.annotations.onRPC;
import controllers.wamplay.annotations.onSubscribe;
import controllers.wamplay.controllers.WAMPlayContoller;

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