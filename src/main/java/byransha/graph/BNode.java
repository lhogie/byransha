package byransha.graph;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.Icon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BNode.exportNodeAction.CSVStream;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.NodeEndpoint;
import graph.BVertex;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import toools.Stop;
import toools.gui.Utilities;

public abstract class BNode {

	public static class Delete extends ConfirmRequiredNodeAction {

		public Delete(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "delete from the graph";
		}

		@Override
		protected ActionResult execConfirm() {
			delete();
			return null;
		}

	}

	public final static class exportNodeAction extends NodeAction<BNode, ListNode<byransha.nodes.primitive.TextNode>> {
		public exportNodeAction(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "export this node as CSV";
		}

		public static class CSVStream {
			String name;
			String data;
		}

		@Override
		public ActionResult<BNode, ListNode<byransha.nodes.primitive.TextNode>> exec(BNode target) throws Throwable {
			var r = new ListNode<byransha.nodes.primitive.TextNode>(g);
			var csvs = new ArrayList<CSVStream>();
			target.toCSVStreams(csvs, true);
			csvs.stream().map(csv -> new byransha.nodes.primitive.TextNode(g, csv.name + "(CSV)", csv.data))
					.forEach(n -> r.get().add(n));
			r.get().add(new byransha.nodes.primitive.TextNode(g, id() + " (JSON)", toJSONNode(0).toPrettyString()));
			return new exportNodeResult(g, this, r);
		}
	}

	final static class ResetNodeAction extends NodeAction {
		public ResetNodeAction(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "reset the values";
		}

		@Override
		public ActionResult exec(BNode target) {
			target.forEachOutField(f -> {
				try {
					var v = (BNode) f.get(target);

					if (v instanceof ValuedNode vn) {
						vn.set(vn.defaultValue());
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});
			return null;
		}
	}

	public BBGraph g;
	public boolean readOnly;
	private int id;
	public boolean historize = true;

	protected BNode(BBGraph g) {
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

	public User currentUser() {
		return g.systemNode == null ? null : g.systemNode.getCurrentUser();
	}

	public void delete() {
		computeIns().forEach(inArc -> inArc.source().removeOut(this));
		g.removeNode(id());
	}

	public void toCSVStreams(List<CSVStream> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
		var c = new CSVStream();
		c.data = fieldsToCSV(printHeaders);
		c.name = "fields";
		l.add(c);
	}

	public String fieldsToCSV(boolean printHeaders) throws IllegalArgumentException, IllegalAccessException {
		var sw = new StringWriter();
		var pw = new PrintWriter(sw);
		fieldsToCSV(pw, printHeaders);
		var s = pw.toString();
		pw.close();
		return s;

	}

	public void fieldsToCSV(PrintWriter ps, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
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

	public void removeOut(BNode out) {
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
		forEachBNodeClass(c -> {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0)
					continue;

				f.setAccessible(true);

				if (BNode.class.isAssignableFrom(f.getType())) {
					consumer.accept(f);
				}
			}
		});
	}

	public List<NodeAction> actions() {
		var r = new ArrayList<NodeAction>();

		forEachBNodeClass(c -> {
			NodeAction.actions.getOrDefault(c, (List<Class>) Collections.EMPTY_LIST).forEach(v -> {
				try {
					r.add((NodeAction) v.getConstructor(BBGraph.class).newInstance(g));
				} catch (Throwable err) {
					throw err instanceof RuntimeException re ? re : new IllegalStateException(err);
				}
			});
		});

		return r;
	}

	public List<NodeView> views() {
		var r = new ArrayList<NodeView>();

		forEachBNodeClass(c -> {
			NodeView.views.getOrDefault(c, (List<Class>) Collections.EMPTY_LIST).forEach(v -> {
				try {
					r.add((NodeView) v.getConstructor(BBGraph.class).newInstance(g));
				} catch (Throwable err) {
					throw err instanceof RuntimeException re ? re : new IllegalStateException(err);
				}
			});
		});

		return r;
	}

	private void forEachBNodeClass(Consumer<Class<? extends BNode>> consumer) {
		for (Class c = getClass(); c != BNode.class; c = c.getSuperclass()) {
			consumer.accept(c);
		}

		consumer.accept(BNode.class);
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
		return id;
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

	public Color getColor() {
		return Color.white;
	}

	public Icon getIcon() {
		return null;
	}

	public abstract String prettyName();

	public boolean isReadOnly() {
		return readOnly;
	}

	public final List<NodeError> errors(int depth) {
		var errs = new ArrayList<NodeError>();
		fillErrors(errs, depth);
		return errs;
	}

	protected void fillErrors(List<NodeError> errs, int depth) {
		bfs(depth, n -> true, (n, d) -> fillErrors(errs, depth));
	}

	public Map<String, BNode> computeOuts() {
		var r = new HashMap<String, BNode>();
		forEachOut((role, out) -> r.put(role, out));
		return r;
	}

	final static JsonNodeFactory factory = new JsonNodeFactory(true);

	public ObjectNode toJSONNode(int depth) {
		ObjectNode r = new ObjectNode(factory);
		r.put("id", id());
		r.put("class", getClass().getName());
		r.put("color", Utilities.toRGBHex(getColor()));
		r.put("prettyName", prettyName());
		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(currentUser()));
		r.put("canEdit", canEdit(currentUser()));
		r.set("actions", new ArrayNode(null, actions().stream().map(e -> (JsonNode) new TextNode(e.name())).toList()));
		r.set("errors", new ArrayNode(null, errors(0).stream().map(err -> (JsonNode) new TextNode(err.msg)).toList()));
		r.set("views", new ArrayNode(null, views().stream().map(v -> v.toJSON(this)).toList()));

		ObjectNode outsNode = new ObjectNode(null);
		r.set("outs", outsNode);

		if (depth > 0) {
			forEachOut((s, o) -> outsNode.set(s, o.toJSONNode(depth - 1)));
		}

		return r;
	}

}
