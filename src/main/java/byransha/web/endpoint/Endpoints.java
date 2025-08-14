package byransha.web.endpoint;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.*;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public class Endpoints extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "list endpoints";
	}

	public Endpoints(BBGraph db) {
		super(db);
		endOfConstructor();
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange http, BNode n) {
		var data = new ArrayNode(null);

		graph.findAll(NodeEndpoint.class, e -> true).stream()
				.filter(e -> e.canExec(user))
				.filter(e -> {
					if (in.has("type")) {
						try {
							Class<?> clazz = Class.forName(in.get("type").asText());
							return clazz.isInstance(e);
						} catch (ClassNotFoundException ex) {
							return false;
						}
					}
					return true;
				})
                .filter(e -> {
                    if (in.has("only_applicable")) {
                        return n.matches(e);
                    }
                    return true;
                })
				.forEach(e -> {
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
				});

		return new EndpointJsonResponse(data, this);
	}
}
