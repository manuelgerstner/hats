package wamplay.controllers.messageHandlers;

import java.util.ArrayList;
import java.util.List;

import wamplay.callbacks.RPCCallback;
import wamplay.models.RPC;
import wamplay.models.WAMPlayClient;
import wamplay.models.messages.CallError;
import wamplay.models.messages.CallResult;

import com.fasterxml.jackson.databind.JsonNode;


public class RPCHandler implements MessageHandler{

	@Override
	public void process(WAMPlayClient client, JsonNode message) {
		String callID = message.get(1).asText();
		String procURI = message.get(2).asText();

		List<JsonNode> args = new ArrayList<JsonNode>();

		for (int i = 3; i < message.size(); i++) {
			args.add(message.get(i));
		}

		RPCCallback cb = RPC.getCallback(procURI);
		if (cb == null) {
			client.send(new CallError(callID, procURI, "404", "RPC method not found!").toJson());
			return;
		}

		try {
			JsonNode response = cb.call(client.getSessionID(), args.toArray(new JsonNode[args.size()]));
			client.send(new CallResult(callID, response).toJson());
		} catch (IllegalArgumentException e) {
			CallError resp;
			if (e.getMessage() == null) {
				resp = new CallError(callID, procURI, "400");
			} else {
				resp = new CallError(callID, procURI, "400", e.getMessage());
			}
			client.send(resp.toJson());
		} catch (Throwable e) {
			CallError resp = new CallError(callID, procURI, "500", e.toString());
			client.send(resp.toJson());
		}
	}

}
