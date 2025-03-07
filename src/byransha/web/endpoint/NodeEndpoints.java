package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.EndPoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class NodeEndpoints extends EndPoint {

	public NodeEndpoints(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange http) {
		var currentNode = user.currentNode();

		if (currentNode == null) {
			return null;
		} else {
			var data = new ArrayNode(null);
			webServer.endpoints.values().stream().filter(e -> e instanceof NodeEndpoint v && currentNode.matches(v))
					.forEach(e -> {
						data.add(new TextNode(e.name()));
					});

			return new EndpointJsonResponse(data, this);
		}
	}

}
