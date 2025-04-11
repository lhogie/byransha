package byransha.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public class Views extends NodeEndpoint<BNode> implements View {

	public Views(BBGraph db) {
		super(db);
	}

	@Override
	public String whatIsThis() {
		return "Description of Views";
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BNode currentNode) {
		ArrayNode viewsNode = new ArrayNode(null);

		for (var e : graph.endpointsUsableFrom(currentNode)) {
			var ev = new ObjectNode(null);
			ev.set("pretty_name", new TextNode(e.prettyName()));
			ev.set("id", new TextNode("" + e.id()));
			ev.set("target", new TextNode(e.getTargetNodeType().getName()));
			ev.set("can read", new TextNode("" + e.canSee(user)));
			ev.set("can write", new TextNode("" + e.canSee(user)));

			if (e.getClass() != Views.class && e instanceof View v && v.sendContentByDefault()) {
				try {
					ev.set("result", e.exec(inputJson, user, webServer, exchange, user).toJson());
				} catch (Throwable err) {
					err.printStackTrace();
					var sw = new StringWriter();
					err.printStackTrace(new PrintWriter(sw));
					ev.set("error", new TextNode(sw.toString()));
				}
			}

			viewsNode.add(ev);
		}

		return new EndpointJsonResponse(viewsNode, this);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}