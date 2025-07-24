package byransha.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EndpointJsonResponse extends EndpointResponse<JsonNode> {

    public enum dialects {
        xy,
        distribution,
        nivoNetwork,
    }

    final String dialect;

    public EndpointJsonResponse(JsonNode d, String dialect) {
        super(d, "application/json");
        this.dialect = dialect;
    }

    public EndpointJsonResponse(JsonNode d, String dialect, int statusCode) {
        super(d, "application/json", statusCode);
        this.dialect = dialect;
    }

    public EndpointJsonResponse(JsonNode d, dialects dialect) {
        this(d, dialect.name());
    }

    public EndpointJsonResponse(JsonNode d, dialects dialect, int statusCode) {
        this(d, dialect.name(), statusCode);
    }

    public EndpointJsonResponse(JsonNode d, NodeEndpoint e) {
        this(d, e.name());
    }

    public EndpointJsonResponse(JsonNode d, NodeEndpoint e, int statusCode) {
        this(d, e.name(), statusCode);
    }

    @Override
    public ObjectNode toJson() {
        var r = super.toJson();

        r.set("dialect", nodeFactory.textNode(dialect));
        return r;
    }

    @Override
    public JsonNode data() {
        return data;
    }

    @Override
    public String toRawText() {
        StringBuilder sb = new StringBuilder(dialect);
        sb.append('\n').append(data.toPrettyString());
        return sb.toString();
    }
}
