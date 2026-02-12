package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import toools.Stop;

public class Endpoints extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "list endpoints";
	}

	public Endpoints(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange http, BNode n) {
		var data = new ArrayNode(null);

		g.forEachNodeOfClass(NodeEndpoint.class, e -> {
			if (!e.canExec(user))
				return Stop.no;

			if (in.has("type")) {
				try {
					Class<?> clazz = Class.forName(in.get("type").asText());
					if (!clazz.isInstance(e)) {
						return Stop.no;
					}
				} catch (ClassNotFoundException ex) {
					return Stop.no;
				}
			}

			if (in.has("only_applicable")) {
				if (!n.matches(e)) {
					return Stop.no;
				}
			}

			var nn = new ObjectNode(null);
			nn.set("name", new TextNode(e.name()));
			nn.set("id", new IntNode(e.id()));
			nn.set("implementation_class", new TextNode(e.getClass().getName()));
			nn.set("endpoint_target_type", new TextNode(e.getTargetNodeType().getName()));
			nn.set("applicable_to_current_node", BooleanNode.valueOf(n.matches(e)));
			nn.set("description", new TextNode(e.whatIsThis()));
			nn.set("pretty_name", new TextNode(e.prettyName()));
			nn.set("type", new TextNode(e.type().toString()));
			data.add(nn);
			return Stop.no;
		});

		in.removeAll();
		return new EndpointJsonResponse(data, this);
	}
}
