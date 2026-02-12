package byransha.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class EndpointResponse<R> {
	protected static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
	public final String contentType;
	public final R data;
	public int statusCode = 200;

	public EndpointResponse(R d, String contentType) {
		this.data = d;
		this.contentType = contentType;
	}

	public EndpointResponse(R d, String contentType, int statusCode) {
		this.data = d;
		this.contentType = contentType;
		this.statusCode = statusCode;
	}

	public ObjectNode toJson() {
		ObjectNode r = nodeFactory.objectNode();
		r.set("contentType", nodeFactory.textNode(contentType));
		r.set("data", data());
		return r;
	}

	public abstract String toRawText();

	public abstract JsonNode data();

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
