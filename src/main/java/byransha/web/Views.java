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

	public Views(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String whatItDoes() {
		return "lists views";
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BNode currentNode) {
		ArrayNode viewsNode = new ArrayNode(null);

		if (currentNode == null) {
			currentNode = graph.root();
		}

		for (var e : graph.endpointsUsableFrom(currentNode)) {
			if (e.canSee(user) && e.canExec(user)) {
				var ev = new ObjectNode(null);
				ev.set("pretty_name", new TextNode(e.prettyName()));
				ev.set("id", new TextNode("" + e.id()));
				ev.set("target", new TextNode(e.getTargetNodeType().getName()));
				ev.set("can read", new TextNode("" + e.canSee(user)));
				ev.set("can write", new TextNode("" + e.canSee(user)));
				ev.set("response_type", new TextNode(e.type().name()));

				if (e.getClass() != Views.class && e instanceof View v && v.sendContentByDefault()) {
					try {
						EndpointResponse result = e.exec(inputJson.deepCopy(), user, webServer, exchange,
								user.currentNode());
						ev.set("result", result.toJson());
					} catch (SecurityException secEx) {
						ev.set("error", new TextNode("Execution blocked: " + secEx.getMessage()));
						ev.set("error_type",
								new TextNode(
										secEx.getMessage().startsWith("Authentication required") ? "AuthenticationError"
												: "AuthorizationError"));
					} catch (Throwable err) {
						err.printStackTrace();
						var sw = new StringWriter();
						err.printStackTrace(new PrintWriter(sw));
						ev.set("error", new TextNode(sw.toString()));
						ev.set("error_type", new TextNode("ExecutionError"));
					}
				}

				viewsNode.add(ev);
			}
		}

		return new EndpointJsonResponse(viewsNode, this);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}