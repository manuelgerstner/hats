package controllers;

import play.mvc.WebSocket;
import ws.wamplay.controllers.WAMPlayServer;

import com.fasterxml.jackson.databind.JsonNode;

public class MyWAMPlayServer extends WAMPlayServer {

	public static WebSocket<JsonNode> connect() {
		WebSocket<JsonNode> ws = WAMPlayServer.connect();
		return ws;
	}

}
