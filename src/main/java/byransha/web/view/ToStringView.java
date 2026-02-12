package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class ToStringView extends NodeEndpoint<BNode> implements TechnicalView {

	public ToStringView(BBGraph db) {
		super(db);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointTextResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			BNode node) {
		return new EndpointTextResponse("text/plain", pw -> pw.print(node.toString()));
	}

	@Override
	public String whatItDoes() {
		return "gets the result of toString() on any node";
	}
}