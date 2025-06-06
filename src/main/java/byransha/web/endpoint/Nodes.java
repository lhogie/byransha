package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class Nodes extends NodeEndpoint<BBGraph> implements View {

	@Override
	public String whatItDoes() {
		return "list all nodes in the graph";
	}

	public Nodes(BBGraph db) {
		super(db);
	}

	public Nodes(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BBGraph g) {
		var a = new ArrayNode(null);

		g.forEachNode(n -> {
			if (n.canSee(user)) {
				var nn = new ObjectNode(null);
				nn.set("id", new TextNode("" + n.id()));
				nn.set("description", new TextNode(n.whatIsThis()));
				nn.set("class", new TextNode(n.getClass().getName()));
				nn.set("to_string", new TextNode(n.toString()));
				nn.set("pretty_name", new TextNode(n.prettyName()));
				a.add(nn);
			}
		});

		return new EndpointJsonResponse(a, this);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}
