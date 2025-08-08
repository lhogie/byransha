package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.ErrorResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Jump extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "navigating to a target node.";
	}

	public Jump(BBGraph g) {
		super(g);
	}


	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		if(node != user.currentNode()){
			user.stack.add(node);
		}

		NodeInfo nodeInfoEndpoint = graph.findEndpoint(NodeInfo.class);
		if (nodeInfoEndpoint == null) {
			return ErrorResponse.serverError("NodeInfo endpoint not found in the graph.");
		}
		in.removeAll();

		return nodeInfoEndpoint.exec(in, user, webServer, exchange, node);
	}
}
