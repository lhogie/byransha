package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.Endpoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.View;
import byransha.web.WebServer;

public class NodeIDs extends Endpoint<BBGraph> implements View {

	public NodeIDs(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BBGraph g) {
		var a = new ArrayNode(null);

		for (var n : g.nodes) {
			a.add(new TextNode("" + n.id()));
		}

		return new EndpointJsonResponse(a, this);
	}
}
