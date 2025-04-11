package byransha.web.endpoint;

import java.util.Objects;

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
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
			BNode currentNode) throws Throwable {
		var targetID = requireParm(in, "target").asInt();
		var target = node(targetID);
		Objects.requireNonNull(target, "no such node: " + targetID);
		//user.stack.push(target); //not effective jump because we go through CurrentNode
		return graph.findEndpoint(CurrentNode.class).exec(in, user, webServer, exchange, target);
	}
}
