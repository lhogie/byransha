package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Jump extends NodeEndpoint<BNode> {

	@Override
	public String whatIsThis() {
		return "Jump endpoint for navigating to a target node.";
	}

	public Jump(BBGraph g) {
		super(g);
	}

	public Jump(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		user.stack.push(node);
		return graph.findEndpoint(NodeInfo.class).exec(in, user, webServer, exchange, node);
	}
}
