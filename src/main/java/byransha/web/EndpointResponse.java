package byransha.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public abstract class EndpointResponse<R> {
	public final String contentType;
	public final R data;
	public int statusCode = 200; // Default status code is 200 OK

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
		ObjectNode r = new ObjectNode(null);
		r.set("contentType", new TextNode(contentType));
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
