package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class IntrospectingEndpoint extends NodeEndpoint<BNode> implements View {

	public IntrospectingEndpoint(BBGraph g) {
		super(g);
	}

	@Override
	public String getDescription() {
		return "introspect the current node and generates the HTML describing it";
	}

	@Override
	public EndpointTextResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n)
			throws Throwable {

		return new EndpointTextResponse("text/html", pw -> {
			pw.println("<ul>");
			n.forEachOut((name, out) -> {
				pw.println("<li>"+name + ": " + out);
			});
			pw.println("</ul>");
		});

	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}
