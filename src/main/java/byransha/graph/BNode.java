package byransha.graph;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BNode.exportNodeAction.CSVStream;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.NodeEndpoint;
import graph.BVertex;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import toools.Stop;
import toools.gui.Utilities;
import toools.io.Cout;

public abstract class BNode {

	public static class Delete extends ConfirmRequiredNodeAction {

		public Delete(BBGraph g, User creator) {
			super(g, creator);
		}

		@Override
		public String whatItDoes() {
			return "delete from the graph";
		}

		@Override
		protected ActionResult execConfirm(User user) {
			delete(user);
			return null;
		}

	}

	public final static class exportNodeAction extends NodeAction<BNode, ListNode<byransha.nodes.primitive.TextNode>> {
		protected exportNodeAction(BBGraph g, User creator) {
			super(g, creator);
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
		public ActionResult<BNode, ListNode<byransha.nodes.primitive.TextNode>> exec(BNode target, User user) throws Throwable {
			var r = new ListNode<byransha.nodes.primitive.TextNode>(g, user);
			var csvs = new ArrayList<CSVStream>();
			target.toCSVStreams(csvs, true);
			csvs.stream().map(csv -> new byransha.nodes.primitive.TextNode(g, user, csv.name + "(CSV)", csv.data))
					.forEach(n -> r.get().add(n));
			r.get().add(new byransha.nodes.primitive.TextNode(g, user, id() + " (JSON)",
					toJSONNode(user, 0).toPrettyString()));
			return new exportNodeResult(g, user, this, r);
		}
	}

	final static class ResetNodeAction extends NodeAction {
		protected ResetNodeAction(BBGraph g, User creator) {
			super(g, creator);
		}

		@Override
		public String whatItDoes() {
			return "reset the values";
		}

		@Override
		public ActionResult exec(BNode target, User user) {
			target.forEachOutField(f -> {
				try {
					var v = (BNode) f.get(target);

					if (v instanceof ValuedNode vn) {
						vn.set(vn.defaultValue(), user);
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

	public void delete(User user) {
		computeIns().forEach(inArc -> inArc.source().removeOut(this, user));
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
			System.out.println(c);
			for (var f : c.getDeclaredFields()) {
				System.out.println("field: " + f.getName() + " tyoe: " + f.getType());
				if ((f.getModifiers() & Modifier.STATIC) != 0 && NodeAction.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);

					try {
						NodeAction action = (NodeAction) f.get(null);
						action.name = f.getName();
					} catch (Throwable e) {
						throw e instanceof RuntimeException re ? re : new RuntimeException(e);
					}
				}
			}
		});

		return r;
	}

	public List<NodeView> views() {
		Cout.debugSuperVisible("Viewww");
		var r = new ArrayList<NodeView>();

		forEachBNodeClass(c -> {
			for (var f : c.getDeclaredFields()) {
				if ((f.getModifiers() & Modifier.STATIC) != 0 && NodeView.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);

					try {
						NodeAction action = (NodeAction) f.get(null);
						action.name = f.getName();
					} catch (Throwable e) {
						throw e instanceof RuntimeException re ? re : new RuntimeException(e);
					}
				}
			}
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

	public ObjectNode toJSONNode(User user, int depth) {
		ObjectNode r = new ObjectNode(null);
		r.put("id", id());
		r.put("class", getClass().getName());
		r.put("color", Utilities.toRGBHex(getColor()));
		r.put("prettyName", prettyName());
		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(user));
		r.put("canEdit", canEdit(user));
		r.set("actions", new ArrayNode(null, actions().stream().map(e -> (JsonNode) new TextNode(e.name)).toList()));
		r.set("errors", new ArrayNode(null, errors(0).stream().map(err -> (JsonNode) new TextNode(err.msg)).toList()));
		r.set("views", new ArrayNode(null, views().stream().map(v -> v.toJSON(user, this)).toList()));

		ObjectNode outsNode = new ObjectNode(null);
		r.set("outs", outsNode);

		if (depth > 0) {
			forEachOut((s, o) -> outsNode.set(s, o.toJSONNode(user, depth - 1)));
		}

		return r;
	}

}
