package byransha.nodes.system;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ColorNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class UIPreferences extends SystemB {
	ColorNode backgroundColor;
	ColorNode textColor;

	public UIPreferences(BBGraph g) {
		super(g);
		backgroundColor = new ColorNode(g, g.systemUser);
		backgroundColor.set("#A9A9A9", g.systemUser);
		textColor = new ColorNode(g, g.systemUser);
		textColor.set("#000000", g.systemUser);
	}

	@Override
	public String prettyName() {
		return "UI preferences";
	}

	@Override
	public String whatIsThis() {
		return "UI preferences";
	}

	public static class getProperties extends NodeEndpoint<BNode> {

		public getProperties(BBGraph db) {
			super(db);
		}

		@Override
		public String whatItDoes() {
			return "gets UI properties for the Web frontend";
		}

		@Override
		public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
				BNode node) {
			var r = new ObjectNode(null);
			r.set("bg_color", new TextNode(g.systemNode.webServer.ui.backgroundColor.get()));
			r.set("text_color", new TextNode(g.systemNode.webServer.ui.backgroundColor.get()));
			return new EndpointJsonResponse(r, this);
		}
	}
}
