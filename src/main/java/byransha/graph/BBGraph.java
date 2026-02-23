package byransha.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.nodes.system.Byransha;
import byransha.nodes.system.SystemNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.View;
import byransha.web.WebServer;
import toools.Stop;

public class BBGraph extends BNode {
	private final ConcurrentMap<Integer, BNode> nodesById = new ConcurrentHashMap<>();

	private int nextID = 0;
	public final SystemNode systemNode;
	public final User systemUser = null;

	public BBGraph(File directory) throws Exception {
		super(null);
		setID(0);
		this.systemNode = new SystemNode(g, directory);
	}

	@Override
	public String whatIsThis() {
		return "a graph";
	}

	public List<NodeEndpoint> endpointsUsableFrom(BNode n) {
		List<NodeEndpoint> r = new ArrayList<>();

		forEachNodeOfClass(NodeEndpoint.class, v -> {
			if (v.getTargetNodeType().isAssignableFrom(n.getClass())) {
				r.add(v);
			}

			return Stop.no;
		});

		r.sort((a, b) -> {
			Class<?> aType = a.getTargetNodeType();
			Class<?> bType = b.getTargetNodeType();

			if (aType.equals(bType))
				return 0;
			else if (aType.isAssignableFrom(bType))
				return 1;
			else if (bType.isAssignableFrom(aType))
				return -1;
			else
				return aType.getName().compareTo(bType.getName());
		});

		return r;
	}

	public synchronized int nextID() {
		while (nodesById.containsKey(nextID))
			++nextID;

		return nextID;
	}

	public long countNodes() {
		return nodesById.size();
	}

	public BNode root() {
		return this;
	}

	public BNode findByID(int id) {
		return nodesById.get(id);
	}

	public BNode forEachNode(Function<BNode, Stop> f) {
		for (BNode node : nodesById.values()) {
			if (f.apply(node) == Stop.yes) {
				return node;
			}
		}

		return null;
	}

	public <C extends BNode> C forEachNodeOfClass(Class<C> nodeClass, Function<C, Stop> f) {
		return (C) forEachNode(n -> nodeClass.isAssignableFrom(n.getClass()) ? f.apply((C) n) : Stop.no);
	}

	public <C extends BNode> C findFirst(Class<C> c, Predicate<C> p) {
		return forEachNodeOfClass(c, n -> Stop.stopIf(p.test(n)));
	}

	public <C extends BNode> C findFirstOr(Class<C> c, Predicate<C> p, Supplier<C> defaultValue) {
		var r = findFirst(c, p);
		return r == null && defaultValue != null ? defaultValue.get() : r;
	}

	public List<User> users() {
		return nodesById.values().stream().filter(n -> n instanceof User).map(n -> (User) n).toList();
	}

	public <N extends BNode, NE extends NodeEndpoint<N>> NE findEndpoint(Class<NE> c) {
		return forEachNodeOfClass(c, e -> Stop.yes);
	}

	public NodeEndpoint findEndpoint(String name) {
		return forEachNodeOfClass(NodeEndpoint.class, e -> Stop.stopIf(e.name().equalsIgnoreCase(name)));
	}

	public static class DBView extends NodeEndpoint<BBGraph> implements TechnicalView {

		@Override
		public String whatItDoes() {
			return "gives info on the graph";
		}

		public DBView(BBGraph g) {
			super(g);
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				BBGraph node) throws Throwable {
			return new EndpointTextResponse("text/html", pw -> {
				pw.println("<ul>");
				pw.println("<li>" + g.countNodes() + " nodes");
				Set<String> classNames = new HashSet<>();
				g.forEachNode(n -> {
					classNames.add(n.getClass().getName());
					return Stop.no;
				});
				pw.println("<li>Node classes: <ul>");
				classNames.stream().sorted().forEach(cn -> pw.println("<li>" + cn + "</li>"));
				pw.println("</ul></li>");

				var users = g.users();
				pw.println("<li>" + users.size() + " users: " + users.stream().map(u -> u.name.get()).toList());
				pw.println("</ul>");
			});
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}
	}

	@Override
	public String prettyName() {
		return "graph";
	}

	public static class ClassDistribution extends NodeEndpoint<BBGraph> implements View {

		public ClassDistribution(BBGraph db) {
			super(db);
		}

		@Override
		public String whatItDoes() {
			return "shows distributed for out nodes";
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BBGraph g)
				throws Throwable {
			var d = new Byransha.Distribution<String>();
			g.forEachNode(n -> {
				d.addOccurence(n.getClass().getName());
				return Stop.no;
			});
			return new EndpointJsonResponse(d.toJson(), dialects.distribution);
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}
	}

	public BNode removeNode(int id) {
		return nodesById.remove(id);
	}

	void setID(BNode n, int newID) {
		BNode previous = nodesById.putIfAbsent(newID, this);

		if (previous != null && previous != this)
			throw new IllegalStateException(
					"can't add node " + this + " because its ID " + this.id() + " is already taken by: " + previous);
	}
}
