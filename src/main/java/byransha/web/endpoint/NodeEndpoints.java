package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import toools.Stop;

public class NodeEndpoints extends NodeEndpoint<WebServer> {

	public NodeEndpoints(BBGraph db) {
		super(db);
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
		g.forEachNodeOfClass(NodeEndpoint.class, e -> {
			if (currentNode.matches(e) && e.canExec(user)) {
				data.add(new TextNode(e.name()));
			}

			return Stop.no;
		});

		return new EndpointJsonResponse(data, this);
	}
}
