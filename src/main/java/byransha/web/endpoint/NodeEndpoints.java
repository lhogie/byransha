package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class NodeEndpoints extends NodeEndpoint<WebServer> {


	public NodeEndpoints(BBGraph db) {
		super(db);
		endOfConstructor();
	}


	@Override
	public String whatItDoes() {
		return "NodeEndpoints description";
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange http, WebServer ws) {
		var currentNode = user.currentNode();

		if (currentNode == null) {
			return ErrorResponse.badRequest("User has no current node.");
		}

		var data = new ArrayNode(null);
		g.findAll(NodeEndpoint.class, e -> true).stream()
				.filter(currentNode::matches)
				.filter(e -> e.canExec(user))
				.forEach(e -> data.add(new TextNode(e.name())));

		return new EndpointJsonResponse(data, this);
	}
}
