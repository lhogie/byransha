package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class NodeInfo extends NodeEndpoint<BNode> {

	public NodeInfo(BBGraph db) {
		super(db);
	}

	public NodeInfo(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public String whatIsThis() {
		return "info about a node";
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BNode node) {

		var r = new ObjectNode(null);
		r.set("id", new TextNode("" + node.id()));
		r.set("pretty_name", new TextNode(node.prettyName()));
		r.set("class", new TextNode(node.getClass().getName()));
		r.set("to_string", new TextNode(node.toString()));
		r.set("can read", new TextNode("" + node.canSee(user)));
		r.set("can write", new TextNode("" + node.canSee(user)));

		var outs = new ArrayNode(null);
		node.forEachOut((name, outNode) -> {
			var out = new ObjectNode(null);
			out.set(name, new TextNode("" + outNode.id()));
			outs.add(out);
		});
		r.set("out", outs);
		var ins = new ArrayNode(null);
		node.forEachIn((name, inNode) -> {
			var in = new ObjectNode(null);
			in.set(name, new TextNode("" + inNode.id()));
			ins.add(in);
		});
		r.set("in", ins);

		var a = new ArrayNode(null);

		for (var e : graph.endpointsUsableFrom(node)) {
			a.add(new TextNode(e.name()));
		}

		r.set("views", a);
		return new EndpointJsonResponse(r, this);
	}
}
