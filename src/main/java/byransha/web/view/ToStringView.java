package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class ToStringView extends NodeEndpoint<BNode> implements DevelopmentView {

	public ToStringView(BBGraph db) {
		super(db);
	}

	public ToStringView(BBGraph db, int id) {
		super(db, id);
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
	public String whatIsThis() {
		return "ToStringView for BNode";
	}
}