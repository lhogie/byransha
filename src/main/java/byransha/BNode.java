package byransha;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.View;
import byransha.web.WebServer;
import toools.gui.Utilities;
import toools.reflect.Clazz;

import byransha.annotations.Max;
import byransha.annotations.Min;
import byransha.annotations.Pattern;
import byransha.annotations.Required;
import byransha.annotations.Size;

public abstract class BNode {
	public String comment;
	private List<InLink> ins;
	public final BBGraph graph;
	private final int id;
	public String color = "pink";
	public Boolean deleted = false;

	
	protected BNode(BBGraph g) {
		this(g, g == null ? 0 : g.nextID());
	}

	protected BNode(BBGraph g, int id) {
		this.id = id;

		if (g != null) {
			this.graph = g;
			// g.accept(this);
		} else if (this instanceof BBGraph thisGraph) {
			this.graph = thisGraph;
		} else {
			throw new IllegalStateException();
		}
	}

	public static <N extends BNode> N create(BBGraph g, Class<N> nodeClass) {
		try {
			N newNode = nodeClass.getConstructor(BBGraph.class).newInstance(g);
			newNode.init();
			g.accept(newNode); // add the new node to the graph
			return newNode;
		} catch (Exception e) {
			throw new RuntimeException("Failed to add node of class: " + nodeClass.getName(), e);
		}
	}

	protected void init() {
	}

	public abstract String whatIsThis();

    public record InLink(String role, BNode source) {
        @Override
        public String toString() {
            return source + "." + role;
        }
    }

	public List<InLink> ins() {
		return ins == null ? graph.findRefsTO(this) : ins;
	}

	public void forEachOutNodeField(Consumer<Field> consumer) {
		for (var c : Clazz.bfs(getClass())) {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;

				if (BNode.class.isAssignableFrom(f.getType())) {
					try {
						f.setAccessible(true);
						consumer.accept(f);
					} catch (IllegalArgumentException err) {
						throw new IllegalStateException(err);
					}
				}
			}
		}
	}

	public void forEachOut(BiConsumer<String, BNode> consumer) {
		forEachOutNodeField(f -> {
			try {
				var outNode = (BNode) f.get(this);

				if (outNode != null) {
					consumer.accept(f.getName(), outNode);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	public void forEachIn(BiConsumer<String, BNode> consumer) {
		ins().forEach(r -> consumer.accept(r.role, r.source));
	}

	public boolean isLeaf() {
		return outDegree() == 0;
	}

	public void bfs(Consumer<BNode> consumer) {
		search(consumer, List::removeFirst);
	}

	public void dfs(Consumer<BNode> consumer) {
		search(consumer, List::removeLast);
	}

	private void search(Consumer<BNode> consumer, Function<List<BNode>, BNode> producer) {
		List<BNode> q = new ArrayList<>();
		BNode c = this;
		q.add(c);
		var visited = new HashSet<BNode>();

		while (!q.isEmpty()) {
			c = producer.apply(q);
			consumer.accept(c);
			c.forEachOut((f, n) -> {
				if (!visited.contains(n)) {
					visited.add(n);
					q.add(n);
				}
			});
		}
	}

	public List<BNode> bfs2list() {
		List<BNode> r = new ArrayList<>();
		bfs(r::add);
		return r;
	}

	public LinkedHashMap<String, BNode> outs() {
		var m = new LinkedHashMap<String, BNode>();
		forEachOut(m::put);
		return m;
	}

	public int outDegree() {
		return outs().size();
	}

	public void remove(){
		this.deleted = true;
	}

	public List<SearchResult> search(String query) {
		var r = new ArrayList<SearchResult>();
		bfs(n -> r.add(new SearchResult(query, n, n.distanceToSearchString(query))));
		Collections.sort(r);
		return r;
	}

	public int distanceToSearchString(String s) {
		return 1;
	}

	public boolean canSee(User user) {
		return true;
	}

	public boolean canEdit(User user) {
		return user.isAdmin();
	}

	public boolean matches(NodeEndpoint v) {
		return v.getTargetNodeType().isAssignableFrom(getClass());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + id();
	}

	public final int id() {
		return id;
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof BNode))
			return false;
		return this.hashCode() == obj.hashCode();
	}

	protected boolean hasField(String name) {
		for (var c : Clazz.bfs(getClass())) {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;

				if (f.getName().equals(name)) {
					return true;
				}
			}
		}

		return false;
	}

	public void getFields(int id) {
		for (var c : Clazz.bfs(getClass())) {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0) {
					continue;
				}
				// a completer
			}
		}
	}

	protected void setField(String name, BNode targetNode) {
		for (var c : Clazz.bfs(getClass())) {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;

				if (f.getName().equals(name)) {
					try {
						f.setAccessible(true);
						f.set(this, targetNode);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
	}

	public static class BasicView extends NodeEndpoint<BNode> implements View {
		@Override
		public String whatItDoes() {
			return "";
		}

		public BasicView(BBGraph g) {
			super(g);
		}

		public BasicView(BBGraph g, int id) {
			super(g, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BNode node)
				throws Throwable {
			var n = new ObjectNode(null);
			n.set("class", new TextNode(node.getClass().getName()));
			n.set("id", new TextNode("" + node.id()));
			n.set("comment", new TextNode(node.comment));

			if (node instanceof PersistingNode p) {
				var d = p.directory();

				if (d != null) {
					n.set("directory", new TextNode(d.getAbsolutePath()));
				}
			}

			n.set("out-degree", new TextNode("" + node.outDegree()));
			n.set("outs", new TextNode(
					node.outs().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toList().toString()));
//			n.set("in-degree", new TextNode("" + node.ins().size()));
//			n.set("ins", new TextNode(node.ins().stream().map(e -> e.toString()).toList().toString()));
			n.set("canSee", new TextNode("" + node.canSee(u)));
			n.set("canEdit", new TextNode("" + node.canEdit(u)));
			return new EndpointJsonResponse(n, this);
		}

		@Override
		public boolean sendContentByDefault() {
			return true;
		}

	}

	public static class Navigator extends NodeEndpoint<BNode> implements View {

		@Override
		public String whatItDoes() {
			return "navigates the graph";
		}

		public Navigator(BBGraph g) {
			super(g);
		}

		public Navigator(BBGraph g, int id) {
			super(g, id);
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
			n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o.id())));
			r.set("ins", ins);
			return new EndpointJsonResponse(r, "bnode_nav2");
		}
	}

	public BVertex toVertex() {
		var v = new BVertex("" + id());
		v.label = toString();
		return v;
	}

	public static class InOutsNivoView extends NodeEndpoint<BNode> implements View {

		@Override
		public String whatItDoes() {
			return "generates a NIVO description of the graph";
		}

		public InOutsNivoView(BBGraph db) {
			super(db);
		}

		public InOutsNivoView(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
			var g = new AnyGraph();
			var current = n.toVertex();
			g.addVertex(current);
			current.color = this.color;
			current.size = 20;

			n.forEachOut((s, o) -> {
				if (o.canSee(user) && !(o instanceof ValuedNode<?>)) {
					var noeudOut = g.ensureHasVertex(o);
					noeudOut.color = "grey";
					noeudOut.prettyName = o.prettyName();
					noeudOut.whatIsThis = o.whatIsThis();
					noeudOut.className = o.getClass().getName();
					var a = g.newArc(current, noeudOut);
					a.label = s;
					a.color = "red";
				}
			});

			n.forEachIn((s, i) -> {
				if (i.canSee(user) && !(i instanceof ValuedNode<?>)) {
					var noeudOut = g.ensureHasVertex(i);
					noeudOut.color = i.color;
					noeudOut.prettyName = i.prettyName();
					noeudOut.whatIsThis = i.whatIsThis();
					noeudOut.className = i.getClass().getName();
					var a = g.newArc(current, noeudOut);
					a.style = "dotted";
					a.label = s;
				}
			});

			return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
		}
	}

	public static class OutDegreeDistribution extends NodeEndpoint<BNode> implements View {

		@Override
		public String whatItDoes() {
			return "shows distribution for out nodes";
		}

		public OutDegreeDistribution(BBGraph db) {
			super(db);
		}

		public OutDegreeDistribution(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
				throws Throwable {
			var d = new Byransha.Distribution<Integer>();
			BBGraph g = (node instanceof BBGraph) ? (BBGraph)node : node.graph;
			g.forEachNode(n -> d.addOccurence(n.outDegree()));
			return new EndpointJsonResponse(d.toJson(), dialects.distribution);
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

	}


	public static class ClassDistribution extends NodeEndpoint<BNode> implements View {

		public ClassDistribution(BBGraph db) {
			super(db);
		}

		public ClassDistribution(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public String whatItDoes() {
			return "shows distributed for out nodes";
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
				throws Throwable {
			var d = new Byransha.Distribution<String>();
			BBGraph g = (node instanceof BBGraph) ? (BBGraph)node : node.graph;
			g.forEachNode(n -> d.addOccurence(n.getClass().getName()));
			return new EndpointJsonResponse(d.toJson(), dialects.distribution);
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

	}

	public JsonNode toJSONNode() {
		var n = new ObjectNode(null);
		n.set("id", new IntNode(id()));
		n.set("pretty_name", new TextNode(prettyName()));
		n.set("color", new TextNode(Utilities.toRGBHex(getColor())));
		return n;
	}

	public Color getColor() {
		return Color.white;
	}

	public abstract String prettyName();

	public boolean isValid() {
		for (var c : Clazz.bfs(getClass())) {
			for (var f : c.getDeclaredFields()) {
				try {
					f.setAccessible(true);
					Object value = f.get(this);

					if (f.isAnnotationPresent(Required.class) && (value == null || (value instanceof ValuedNode && ((ValuedNode) value).get() == null))) {
						return false;
					}

					if (value != null) {
						if (f.isAnnotationPresent(Min.class)) {
							Min min = f.getAnnotation(Min.class);
							if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof Number) {
								if (((Number) ((ValuedNode<?>) value).get()).doubleValue() < min.value()) {
									return false;
								}
							}
						}

						if (f.isAnnotationPresent(Max.class)) {
							Max max = f.getAnnotation(Max.class);
							if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof Number) {
								if (((Number) ((ValuedNode<?>) value).get()).doubleValue() > max.value()) {
									return false;
								}
							}
						}

						if (f.isAnnotationPresent(Size.class)) {
							Size size = f.getAnnotation(Size.class);
							int length = -1;

							if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof String) {
								length = ((String) ((ValuedNode<?>) value).get()).length();
							} else if (value instanceof ListNode) {
								length = ((ListNode<?>) value).size();
							} else if (value instanceof SetNode) {
								length = ((SetNode<?>) value).size();
							}

							if (length != -1 && (length < size.min() || length > size.max())) {
								return false;
							}
						}

						if (f.isAnnotationPresent(Pattern.class)) {
							Pattern pattern = f.getAnnotation(Pattern.class);
							if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof String) {
								if (!((String) ((ValuedNode<?>) value).get()).matches(pattern.regex())) {
									return false;
								}
							}
						}
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	/*
	 * public static class BFS extends NodeEndpoint<BNode> {
	 *
	 * @Override public EndpointResponse exec(ObjectNode input, User user, WebServer
	 * webServer, HttpsExchange exchange, ObjectNode r = null;
	 *
	 * List<BNode> q = new ArrayList<>(); BNode c = n; q.add(c); var visited = new
	 * Int2ObjectOpenHashMap<ObjectNode>();
	 *
	 * while (!q.isEmpty()) { c = q.remove(0); var nn = visited.put(c.id(), new
	 * ObjectNode(null)); r.add(nn);
	 *
	 * c.forEachOut((f, out) -> { if (!visited.containsKey(out)) { visited.add(new
	 * ObjectNode(null)); q.add(out); } }); }
	 *
	 * var outs = new ObjectNode(null); n.forEachOut((name, o) -> outs.set(name, new
	 * TextNode("" + o))); r.set("outs", outs); var ins = new ObjectNode(null);
	 * n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o))); r.set("ins",
	 * ins); return r; }
	 *
	 * @Override public String whatIsThis() { return
	 * "generates a JSON describing the local node and its out-nodes, up to a given depth"
	 * ; }
	 *
	 * }
	 */

}
