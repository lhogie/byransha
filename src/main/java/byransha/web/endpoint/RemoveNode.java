package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class RemoveNode extends NodeEndpoint<BNode> {

	public RemoveNode(BBGraph g) {
		super(g);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			BNode node) throws Throwable {

		if (node instanceof BBGraph)
			return new EndpointJsonResponse(new ObjectNode(null).set("id", new IntNode(node.id())),
					"Node cannot be removed because it is the graph.");

		node.delete.exec(user);
		user.stack.add(g);
		var a = new ArrayNode(null);
		return new EndpointJsonResponse(new TextNode("ok"), RemoveNode.class.getName());
	}

	@Override
	public String whatItDoes() {
		return "removes a node from the graph.";
	}
}
