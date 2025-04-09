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

public class CurrentNode extends NodeEndpoint<BNode> {

	@Override
	public String getDescription() {
		return "CurrentNode description";
	}

	public CurrentNode(BBGraph db) {
		super(db);
	}

	public CurrentNode(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode inputJson, User user, WebServer webServer, HttpsExchange exchange,
			BNode currentNode) {

		if(user.stack.lastElement() != currentNode){
			user.stack.push(currentNode);
		}

		var r = new ObjectNode(null);
		r.set("id", new TextNode("" + currentNode.id()));
		r.set("class", new TextNode(currentNode.getClass().getName()));
		r.set("to_string", new TextNode(currentNode.toString()));
		r.set("can read", new TextNode("" + currentNode.canSee(user)));
		r.set("can write", new TextNode("" + currentNode.canSee(user)));

		var outs = new ArrayNode(null);
		currentNode.forEachOut((name, outNode) -> {
			var out = new ObjectNode(null);
			out.set(name, new TextNode("" + outNode.id()));
			outs.add(out);
		});
		r.set("out", outs);
		var ins = new ArrayNode(null);
		currentNode.forEachIn((name, inNode) -> {
			var in = new ObjectNode(null);
			in.set(name, new TextNode("" + inNode.id()));
			ins.add(in);
		});
		r.set("in", ins);

		var a = new ArrayNode(null);

		for (var e : webServer.endpointsUsableFrom(currentNode)) {
			a.add(new TextNode(e.name()));
		}

		r.set("views", a);
		return new EndpointJsonResponse(r, this);
	}
}
