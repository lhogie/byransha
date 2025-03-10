package byransha;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph.Ref;
import byransha.graph.BGraph;
import byransha.graph.BVertex;
import byransha.web.Endpoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.View;
import byransha.web.WebServer;
import toools.reflect.Clazz;

public class BNode {
	public String comment;
	private List<Ref> refs;
	public final BBGraph graph;
	private int id;

	public BNode(BBGraph g) {
		if (g != null) {
			id = g.idCount++;
			this.graph = g;
			g.accept(this);
		} else {
			if (this instanceof BBGraph bbg) {
				id = 0;
				graph = bbg;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	public List<Ref> ins() {
		return refs == null ? graph.findRefsTO(this) : refs;
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
		search(consumer, q -> q.remove(0));
	}

	public void dfs(Consumer<BNode> consumer) {
		search(consumer, q -> q.remove(q.size() - 1));
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
		bfs(n -> r.add(n));
		return r;
	}

	public LinkedHashMap<String, BNode> outs() {
		var m = new LinkedHashMap<String, BNode>();
		forEachOut((name, o) -> m.put(name, o));
		return m;
	}

	public int outDegree() {
		AtomicInteger i = new AtomicInteger(0);
		forEachOut((name, prop) -> i.incrementAndGet());
		return i.get();
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
		return false;
	}

	public boolean matches(Endpoint<?> v) {
		return v.getTargetNodeType().isAssignableFrom(getClass());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + id();
	}

	public void saveOuts(Consumer<File> writingFiles) {
		var outD = outsDirectory();

		if (!outD.exists()) {
			writingFiles.accept(outD);
			outD.mkdirs();
		}

		forEachOut((name, outNode) -> {
			try {
				var symlink = new File(outD, name);// + "@" + outNode.id());
				writingFiles.accept(symlink);
				Files.createSymbolicLink(symlink.toPath(), outNode.directory().toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void saveIns(Consumer<File> writingFiles) {
		var inD = new File(directory(), "ins");

		if (!inD.exists()) {
			writingFiles.accept(inD);
			inD.mkdirs();
		}

		forEachIn((name, inNode) -> {
			try {
				var symlink = new File(inD, name);
				writingFiles.accept(symlink);
				System.err.println(symlink.toPath());
				System.err.println(inNode.directory().toPath());
				Files.createSymbolicLink(symlink.toPath(), inNode.directory().toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public File directory() {
		if (graph == null)
			return null;

		if (graph.directory == null)
			return null;

		return new File(graph.directory, getClass().getName() + "/." + id());
	}

	public File outsDirectory() {
		var d = directory();
		return d == null ? null : new File(directory(), "outs");
	}

	public final int id() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public final boolean equals(Object obj) {
		return this.hashCode() == ((BNode) obj).hashCode();
	}

	public static class BasicView extends Endpoint<BNode> implements View {
		public BasicView(BBGraph g) {
			super(g);
			sendContentByDefault = true;
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BNode node)
				throws Throwable {
			var n = new ObjectNode(null);
			n.set("class", new TextNode(node.getClass().getName()));
			n.set("id", new TextNode("" + node.id()));
			n.set("comment", new TextNode(node.comment));
			n.set("directory", new TextNode(node.directory().getAbsolutePath()));
			n.set("out-degree", new TextNode("" + node.outDegree()));
			n.set("outs", new TextNode(
					node.outs().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toList().toString()));
//			n.set("in-degree", new TextNode("" + node.ins().size()));
//			n.set("ins", new TextNode(node.ins().stream().map(e -> e.toString()).toList().toString()));
			n.set("canSee", new TextNode("" + node.canSee(u)));
			n.set("canEdit", new TextNode("" + node.canEdit(u)));
			return new EndpointJsonResponse(n, this);
		}
	}

	public static class Nav2 extends Endpoint<BNode> implements View {
		public Nav2(BBGraph g) {
			super(g);
			sendContentByDefault = true;
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
			return new EndpointJsonResponse(r, this);
		}
	}

	public BVertex toVertex() {
		var v = new BVertex("" + id());
		v.label = toString();
		return v;
	}

	public static class GraphView extends Endpoint<BNode> implements View {

		public GraphView(BBGraph db) {
			super(db);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User u, WebServer webServer, HttpsExchange exchange, BNode n) {
			var g = new BGraph();
			g.addVertex(n.toVertex());

			n.forEachOut((s, o) -> {
				var a = g.newArc(g.ensureHasVertex(n), g.ensureHasVertex(o));
				a.label = s;
			});

			n.forEachIn((s, i) -> {
				var a = g.newArc(g.ensureHasVertex(n), g.ensureHasVertex(i));
				a.style = "dotted";
				a.label = s;
			});

			return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
		}
	}

	public static class OutNodeDistribution extends Endpoint<BNode> implements View {

		public OutNodeDistribution(BBGraph db) {
			super(db);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node)
				throws Throwable {
			var d = new Byransha.Distribution<String>();
			forEachOut((n, o) -> d.addOccurence(o.getClass().getName()));
			return new EndpointJsonResponse(d.toJson(), dialects.distribution);
		}
	}

	/*
	 * public static class BFS extends JSONView<BNode> {
	 * 
	 * @Override protected JsonNode jsonData(BNode n, User u) { ObjectNode r = null;
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
	 * 
	 * 
	 * 
	 * var outs = new ObjectNode(null); n.forEachOut((name, o) -> outs.set(name, new
	 * TextNode("" + o))); r.set("outs", outs); var ins = new ObjectNode(null);
	 * n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o))); r.set("ins",
	 * ins); return r; } }
	 */
}
