package byransha.graph;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import graph.AnyGraph;
import graph.BVertex;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import toools.Stop;
import toools.gui.Utilities;

public abstract class BNode {
	public BBGraph g;
	public boolean readOnly;
	private int id;

	protected BNode(BBGraph g, User creator) {
		if (!canCreate(creator))
			throw new IllegalStateException("can't create " + creator);

		if (g == null) {
			this.id = 0;
			this.g = (BBGraph) this;
		} else {
			this.g = g;

			if (this instanceof NodeEndpoint ne) {
				var alreadyInName = this.g.findEndpoint(ne.name());

				if (alreadyInName != null) {
					throw new IllegalArgumentException("adding " + ne + ", endpoint with same name '" + ne.name()
							+ "' already there: " + alreadyInName.getClass().getName());
				}
			}
		}
	}

	public final NodeAction reset = new NodeAction() {

		@Override
		public String description() {
			return "reset the values";
		}

		@Override
		public BNode exec(User user) {
			forEachOutField(f -> {
				try {
					var v = (BNode) f.get(BNode.this);

					if (v instanceof ValuedNode vn) {
						vn.set(vn.defaultValue(), user);
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});
			return null;
		}
	};

	public final NodeAction delete = new NodeAction() {

		@Override
		public String description() {
			return "delete from the graph";
		}

		@Override
		public BNode exec(User user) {
			computeIns().forEach(inArc -> inArc.source.removeOut(BNode.this, user));
			g.removeNode(id());
			return g;
		}
	};

	public final NodeAction export = new NodeAction() {

		@Override
		public String description() {
			return "export this node as CSV";
		}

		@Override
		public BNode exec(User user) throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
			File file = new File(g.systemNode.eventList.directory, getClass().getName() + "-" + id() + ".csv");
			var ps = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
			toCSV(ps, true);
			return null;
		}
	};

	public void toCSV(PrintWriter ps, boolean printHeaders) throws IllegalArgumentException, IllegalAccessException {
		var fields = new ArrayList<Field>();

		if (printHeaders) {
			forEachOutField(f -> fields.add(f));
			ps.println('#' + fields.stream().map(f -> f.getName()).collect(Collectors.joining(", ")));
		}

		for (int i = 0; i < fields.size(); ++i) {
			var f = fields.get(i);
			BNode out = (BNode) f.get(this);
			ps.print(out.toString());

			if (i < fields.size() - 1) {
				ps.print(';');
			} else {
				ps.println();
			}
		}
	}

	public void removeOut(BNode out, User user) {
		forEachOutField(f -> {
			try {
				if (f.get(this) == out) {
					f.set(this, null);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void forEachOutField(Consumer<Field> consumer) {
		boolean bNodeReached = false;

		for (Class c = getClass(); !bNodeReached; c = c.getSuperclass()) {
			bNodeReached = c == BNode.class;

			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;
				f.setAccessible(true);

				if (BNode.class.isAssignableFrom(f.getType())) {
					consumer.accept(f);
				}
			}
		}
	}

	public void forEachOut(BiConsumer<String, BNode> consumer) {
		forEachOutField(f -> {
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

	public void setID(int newID) {
		g.setID(this, newID);
		this.id = newID;

	}

	public abstract String whatIsThis();

	public List<InLink> computeIns() {
		List<InLink> refs = new ArrayList<>();

		g.forEachNode(node -> {
			node.forEachOut((role, target) -> {
				if (target != null && target == this) {
					refs.add(new InLink(role, node));
				}
			});

			return Stop.no;
		});

		return refs;
	}

	public void bfs(int maxDistance, Predicate<BNode> nodeFilter, ObjIntConsumer<BNode> consumer) {
		List<BNode> q = new ArrayList<>();
		var distances = new Object2IntOpenHashMap<BNode>();
		var visited = new HashSet<BNode>();

		BNode c = this;
		q.add(c);
		distances.put(c, 0);

		while (!q.isEmpty()) {
			c = q.removeFirst();
			int d = distances.getInt(c);

			if (d > maxDistance) { // went too far
				break;
			}

			if (d > 0) { // don't expose source node
				consumer.accept(c, d);
			}

			final var c_tmp = c;
			c.forEachOut((f, n) -> {
				if (!visited.contains(n)) {
					visited.add(n);

					if (nodeFilter.test(c_tmp)) {
						q.add(n);
						distances.put(n, d + 1);
					}
				}
			});
		}
	}

	protected void onEdgeChanged(String fieldName, BNode oldTarget, BNode newTarget) {
	}

	public int distanceToSearchString(String s, User user) {
		return 1;
	}

	public boolean canSee(User user) {
		return true;
	}

	public boolean canEdit(User user) {
		return !isReadOnly();
	}

	public boolean canCreate(User user) {
		return true;
	}

	public boolean matches(NodeEndpoint v) {
		return v.getTargetNodeType().isAssignableFrom(getClass());
	}

	@Override
	public String toString() {
		return prettyName();
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
		return this == obj;
	}

	public BVertex toVertex() {
		var v = new BVertex("" + id());
		v.label = toString();
		return v;
	}

	public ObjectNode toJSONNode(User user, int depth) {
		ObjectNode r = new ObjectNode(null);
		r.put("id", id());
		r.put("class", getClass().getName());
		r.put("color", Utilities.toRGBHex(getColor()));
		r.put("prettyName", prettyName());
		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(user));
		r.put("canEdit", canEdit(user));
		r.set("errors", new ArrayNode(null, errors(0).stream().map(e -> (JsonNode) new TextNode(e.error)).toList()));
		r.set("errors", new ArrayNode(null, errors(0).stream().map(e -> (JsonNode) new TextNode(e.error)).toList()));

		ObjectNode outsNode = new ObjectNode(null);
		r.set("outs", outsNode);

		if (depth > 0) {
			forEachOut((s, o) -> outsNode.set(s, o.toJSONNode(user, depth - 1)));
		}

		return r;
	}

	public Color getColor() {
		return g.getColorForNodeClass(getClass());
	}

	public abstract String prettyName();

	public boolean isReadOnly() {
		return readOnly;
	}

	private int bnodeDepth() {
		int r = 0;

		for (Class<?> c = getClass(); c != BNode.class; c = c.getSuperclass()) {
			r++;
		}

		return r;
	}

	public Pair<BNode, Field> getParentNodeWithField() {
		for (InLink inLink : computeIns()) {
			BNode sourceNode = inLink.source();

			// Check fields in the entire class hierarchy
			Class<?> currentClass = sourceNode.getClass();
			while (currentClass != null && currentClass != Object.class) {
				for (Field field : currentClass.getDeclaredFields()) {
					if ((field.getModifiers() & Modifier.STATIC) != 0)
						continue;
					field.setAccessible(true);

					try {
						if (field.get(sourceNode) == this) {
							Pair<BNode, Field> result = Pair.of(sourceNode, field);
							return result;
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException("Error accessing field: " + e.getMessage(), e);
					}
				}
				// Move to the parent class
				currentClass = currentClass.getSuperclass();
			}
		}

		return null;
	}

	public final List<NodeError> errors(int depth) {
		var errs = new ArrayList<NodeError>();
		fillErrors(errs, depth);
		return errs;
	}

	protected void fillErrors(List<NodeError> errs, int depth) {
		bfs(depth, n -> true, (n, d) -> fillErrors(errs, depth));
	}

	public record InLink(String role, BNode source) {
		@Override
		public String toString() {
			return source + "." + role;
		}
	}

	public static class InOutsNivoView extends NodeEndpoint<BNode> implements TechnicalView {

		public InOutsNivoView(BBGraph db) {
			super(db);
		}

		@Override
		public String whatItDoes() {
			return "generates a NIVO description of the graph";
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
			var g = new AnyGraph();
			var currentVertex = g.ensureHasVertex(n);
			setVertexProperties(currentVertex, n, "pink");
			currentVertex.size = 20;
			var limit = 99;
			AtomicInteger currentNumberNodes = new AtomicInteger(0);

			if (n.getClass() != BBGraph.class) {
				n.forEachOut((role, outNode) -> {
					if (currentNumberNodes.get() <= limit || outNode.getClass() == BBGraph.class) {
						var outVertex = g.ensureHasVertex(outNode);
						setVertexProperties(outVertex, outNode, "blue");
						var arc = g.newArc(currentVertex, outVertex);
						arc.label = role;
						arc.color = "red";
						currentNumberNodes.getAndIncrement();
					}
				});

				n.computeIns().forEach(inLink -> {
					if (inLink.source.canSee(user) && !(inLink.source instanceof ValuedNode<?>)) {
						var inVertex = g.ensureHasVertex(inLink.source);
						setVertexProperties(inVertex, inLink.source, "pink");
						var arc = g.newArc(inVertex, currentVertex);
						arc.style = "dotted";
						arc.label = inLink.role;
					}
				});
			}

			return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
		}

		private void setVertexProperties(BVertex vertex, BNode node, String defaultColor) {
			vertex.color = node.getColor().toString();
			vertex.label = node.prettyName();
			vertex.whatIsThis = node.whatIsThis();
			vertex.className = node.getClass().getName();
		}
	}

	public Map<String, BNode> computeOuts() {
		var r = new HashMap<String, BNode>();
		forEachOut((role, out) -> r.put(role, out));
		return r;
	}

}
