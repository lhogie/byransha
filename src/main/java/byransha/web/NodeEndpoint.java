package byransha.web;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public abstract class NodeEndpoint<N extends BNode> extends Endpoint {

	public NodeEndpoint(BBGraph db) {
		super(db);
	}

	@Override
	public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange)
			throws Throwable {
		N n = node(input.remove("node_id"), user);
		return exec(input, user, webServer, exchange, n);
	}

	private N node(JsonNode node, User user) {
		if (node == null) {
			return (N) user.currentNode();
		}

		return (N) node(node.asInt());
	}

	public abstract EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			N node) throws Throwable;

	public <N extends BNode> Class<N> getTargetNodeType() {
		for (Class c = getClass(); c != null; c = c.getSuperclass()) {
			var t = c.getGenericSuperclass();

			if (t instanceof ParameterizedType pt) {
				return (Class<N>) pt.getActualTypeArguments()[0];
			}
		}

		throw new IllegalStateException();
	}

	public BNode node(int id) {
		return graph.findByID(id);
	}

	public List<BNode> nodes(int... ids) {
		return Arrays.stream(ids).mapToObj(id -> node(id)).toList();
	}

	public enum TYPE {
		development, technical, business
	}

	public TYPE type() {
		if (this instanceof DevelopmentView) {
			return TYPE.development;
		} else if (this instanceof TechnicalView) {
			return TYPE.technical;
		} else {
			return TYPE.business;
		}
	}
}
