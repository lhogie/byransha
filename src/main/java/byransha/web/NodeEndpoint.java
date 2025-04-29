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
import toools.reflect.Clazz;

public abstract class NodeEndpoint<N extends BNode> extends Endpoint {

	public NodeEndpoint(BBGraph db) {
		super(db);
	}

	public NodeEndpoint(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public final EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange)
			throws Throwable {
		N n = node(input.remove("node_id"), user);
		return exec(input, user, webServer, exchange, n);
	}

	private N node(JsonNode node, User user) {
		if (node == null) {
			return (N) user.currentNode();
        }

		var s = node.asText();

		try {
			return (N) node(Integer.parseInt(s));
		} catch (NumberFormatException err) {
			Clazz.findClassOrFail(s);
			return null;
		}
	}

	public abstract EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
			N node) throws Throwable;

    public BNode node(int id) {
		return graph.findByID(id);
	}

	public List<BNode> nodes(int... ids) {
		return Arrays.stream(ids).mapToObj(this::node).toList();
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
