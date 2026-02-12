package byransha.web.view;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class ColorNodeView extends NodeEndpoint<BNode> implements TechnicalView {

	public ColorNodeView(BBGraph db) {
		super(db);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		var color = node.getColor();
		String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		return new EndpointJsonResponse(new TextNode(hex), "color");
	}

	@Override
	public String whatItDoes() {
		return "returns the color of a node";
	}
}
