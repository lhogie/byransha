package byransha.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;

public class BasicView extends NodeEndpoint<BNode> implements TechnicalView {

	@Override
	public String whatItDoes() {
		return "show basic info on node";
	}

	public BasicView(BBGraph g) {
		super(g);
	}

	@Override
	public EndpointResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BNode node)
			throws Throwable {
		var n = new ObjectNode(null);
		n.set("class", new TextNode(node.getClass().getName()));
		n.set("id", new TextNode("" + node.id()));
		var outs = node.computeOuts();
		n.set("out-degree", new TextNode("" + outs.size()));
		n.set("outs", new TextNode(
				node.computeOuts().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toList().toString()));
		var ins = node.computeIns();
		n.set("in-degree", new TextNode("" + ins.size()));
		n.set("ins", new TextNode(ins.stream().map(l -> l.source().prettyName()).toList().toString()));
		n.set("canSee", new TextNode("" + node.canSee(u)));
		n.set("canEdit", new TextNode("" + node.canEdit(u)));
		return new EndpointJsonResponse(n, this);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}
}
