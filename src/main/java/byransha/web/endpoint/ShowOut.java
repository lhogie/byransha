package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;

public class ShowOut extends NodeEndpoint<BNode> implements View {

	@Override
	public String whatIsThis() {
		return "Endpoint to show every values of the current node";
	}

	public ShowOut(BBGraph g) {
		super(g);
	}

	public ShowOut(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n)
			throws Throwable {
		var a = new ArrayNode(null);

		n.forEachOut((name, out) -> {
			if (user.canSee(user)) {
				var b = new ObjectNode(null);
				b.set("id", new IntNode(out.id()));
				b.set("name", new TextNode(name));
				b.set("type", new TextNode(out.getClass().getSimpleName()));
				b.set("editable", BooleanNode.valueOf(out.canEdit(user)));

				if (out instanceof ValuedNode vn) {
					b.set("value", new TextNode(vn.get().toString()));
				}
			}

		});

		// System.out.println("id de currentNode:"+ currentNode.id());
		return new EndpointJsonResponse(a, "response for edit");
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}
