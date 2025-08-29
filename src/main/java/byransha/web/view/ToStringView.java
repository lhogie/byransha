package byransha.web.view;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public class ToStringView extends NodeEndpoint<BNode> implements TechnicalView {

	public ToStringView(BBGraph db) {
		super(db);
		endOfConstructor();
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
		return "ToStringView for BNode";
	}
}