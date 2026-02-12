package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Summarizer extends NodeEndpoint<BNode> {

	public Summarizer(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItDoes() {
		return "summarize";
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
			BNode node) {
		var r = new ObjectNode(null);

		forEachOut((name, out) -> {
			if (out instanceof ValuedNode vn) {
				r.put(name, out.prettyName());
			}
		});

		return new EndpointJsonResponse(r, "summarizer");
	}
}
