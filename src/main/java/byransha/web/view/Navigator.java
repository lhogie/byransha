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

public class Navigator extends NodeEndpoint<BNode> implements TechnicalView {

	@Override
	public String whatItDoes() {
		return "navigates the graph";
	}

	public Navigator(BBGraph g) {
		super(g);
	}

	@Override
	public boolean sendContentByDefault() {
		return true;
	}

	@Override
	public EndpointResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BNode n) {
		var r = new ObjectNode(null);
		var outs = new ObjectNode(null);
		n.forEachOut((name, o) -> outs.set(name, new TextNode("" + o.id())));
		r.set("outs", outs);
		var ins = new ObjectNode(null);
		computeIns().forEach(inLink -> ins.set(inLink.role(), new TextNode("" + inLink.source().id())));
		r.set("ins", ins);
		return new EndpointJsonResponse(r, "bnode_nav2");
	}
}
