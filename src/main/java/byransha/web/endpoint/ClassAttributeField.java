package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class ClassAttributeField extends NodeEndpoint<BNode> implements View {

	public ClassAttributeField(BBGraph g) {
		super(g);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		return new EndpointJsonResponse(node.toJSONNode(user, 1), ClassAttributeField.class.getName());
	}

	@Override
	public String whatItDoes() {
		return "gets a description on the current node";
	}
}
