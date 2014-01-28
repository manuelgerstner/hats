package models;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Event {

	public String eventType;
	public Object eventData;

	public Event(String eventType) {

		this.eventType = eventType;
	}

	public Event setType(String eventType) {
		this.eventType = eventType;
		return this;
	}

	public Event setData(Object eventData) {
		this.eventData = eventData;
		return this;
	}

	public JsonNode toJson() throws IllegalArgumentException {
		if (this.eventType == null || this.eventData == null) {
			throw new IllegalArgumentException();
		}

		ObjectNode result = Json.newObject();
		result.put("eventType", this.eventType);

		if (!(this.eventData instanceof ObjectNode)) {
			this.eventData = new ObjectMapper().convertValue(this.eventData,
					JsonNode.class);
		}
		result.put("eventData", (JsonNode) this.eventData);

		return result;

	}
}
