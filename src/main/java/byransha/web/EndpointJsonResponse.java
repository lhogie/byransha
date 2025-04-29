package byransha.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class EndpointJsonResponse extends EndpointResponse<JsonNode> {

	public enum dialects {
		xy, distribution, nivoNetwork
	}

	final String dialect;

	public EndpointJsonResponse(JsonNode d, String dialect) {
		super(d, "text/json");
		this.dialect = dialect;
	}

	public EndpointJsonResponse(JsonNode d, dialects dialect) {
		this(d, dialect.name());
	}

	public EndpointJsonResponse(JsonNode d, NodeEndpoint<?> e) {
		this(d, e.name());
	}

	@Override
	public ObjectNode toJson() {
		var r = super.toJson();
		r.set("dialect", new TextNode(dialect));
		return r;
	}

	@Override
	public JsonNode data() {
		return data;
	}

	@Override
	public String toRawText() {
		return dialect + "\n" + data.toPrettyString();
	}
}
