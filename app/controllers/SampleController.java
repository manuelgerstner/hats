package controllers;

import play.libs.Json;
import ws.wamplay.annotations.URIPrefix;
import ws.wamplay.annotations.onPublish;
import ws.wamplay.annotations.onRPC;
import ws.wamplay.annotations.onSubscribe;
import ws.wamplay.controllers.WAMPlayContoller;

import com.fasterxml.jackson.databind.JsonNode;

@URIPrefix("http://localhost:9000/sample")
public class SampleController extends WAMPlayContoller {

	@onRPC("#meaningOfLife")
	public static String getMeaningOfLife(String sessionID) {
		return "Meaning of life is: 42";
	}

	@onRPC("#capital")
	public static String add(String sessionID, JsonNode[] args) {
		String ans = args[0].asText().toUpperCase();
		return ans;
	}

	@onSubscribe("/chat")
	public static boolean capitalSubscribe(String sessionID) {
		return true;
	}

	@onPublish("/chat")
	public static JsonNode truncatePublish(String sessionID, JsonNode event) {
		return Json.toJson(event);
	}
}