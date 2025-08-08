package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class Summarizer extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "summarize";
	}

	public Summarizer(BBGraph g) {
		super(g);
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
