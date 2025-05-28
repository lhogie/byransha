package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import toools.gui.Utilities;

public class ColorNodeView extends NodeEndpoint<BNode> implements DevelopmentView {

	public ColorNodeView(BBGraph db) {
		super(db);
	}

	public ColorNodeView(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		return new EndpointJsonResponse(new TextNode(Utilities.toRGBHex(node.getColor())), "color");
	}

	@Override
	public String whatItDoes() {
		return "returns the color of a node";
	}
}
