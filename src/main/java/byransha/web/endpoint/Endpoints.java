package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Endpoints extends NodeEndpoint<BNode> {

	@Override
	public String getDescription() {
		return "Endpoints for managing BNode operations.";
	}

	public Endpoints(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange http, BNode n) {
		var data = new ArrayNode(null);
		graph.findAll(NodeEndpoint.class, e -> true).forEach(e -> {
			var nn = new ObjectNode(null);
			nn.set("name", new TextNode(e.name()));
			nn.set("implementation_class", new TextNode(e.getClass().getName()));
			nn.set("endpoint_target_type", new TextNode(e.getTargetNodeType().getName()));
			nn.set("applicable_to_current_node", BooleanNode.valueOf(n.matches(e)));
			nn.set("description", new TextNode(e.getDescription()));
			data.add(nn);
		});

		return new EndpointJsonResponse(data, this);
	}
}
