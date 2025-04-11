package byransha.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

public class AnyGraph {
	public List<BVertex> nodes = new ArrayList<BVertex>();
	public List<BLinks> links = new ArrayList<BLinks>();

	public static void main(String[] args) {
		var g = new AnyGraph();
		g.newArc(new BVertex("a"), new BVertex("b"));
		System.out.println(g.toNivoJSON().toPrettyString());
	}

	public synchronized BLinks newArc(BVertex v, BVertex w) {
		synchronized (nodes) {
			nodes.add(v);
			nodes.add(w);
		}

		synchronized (links) {
			var a = new BLinks(v, w);
			links.add(a);
			return a;
		}
	}

	public synchronized JsonNode toNivoJSON() {
		synchronized (nodes) {
			synchronized (links) {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.valueToTree(this);
			}
		}
	}

	public synchronized void addVertex(BVertex bVertex) {
		synchronized (nodes) {
			nodes.add(bVertex);
		}
	}

	public synchronized BVertex findVertexByID(String id) {
		synchronized (nodes) {
			for (var v : nodes) {
				if (v.id.equals(id)) {
					return v;
				}
			}
			return null;
		}
	}

	public synchronized BVertex ensureHasVertex(BNode o) {
		var v = findVertexByID("" + o.id());

		if (v == null) {
			addVertex(v = o.toVertex());
		}

		return v;
	}

	public static class Classes extends NodeEndpoint<BNode> {

		public Classes(BBGraph db) {
			super(db);
		}

		public Classes(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				BNode node) throws Throwable {
			var a = new ArrayNode(null);
			var classes = new HashSet<Class>();
			graph.forEachNode(n -> classes.add(n.getClass()));
			classes.forEach(c -> a.add(new TextNode(c.getName())));
			return new EndpointJsonResponse(a, this);
		}

		@Override
		public String whatIsThis() {
			return "list all classes in the graph";
		}

	}
}
