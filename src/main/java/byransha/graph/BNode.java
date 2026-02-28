package byransha.graph;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.action.Delete;
import byransha.graph.action.Export;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.Jump;
import byransha.graph.action.Reset;
import byransha.graph.action.search.Search;
import byransha.graph.action.search.SearchRegexp;
import byransha.graph.action.search.SearchText;
import byransha.graph.view.AvailableActionsView;
import byransha.graph.view.DebugView;
import byransha.graph.view.ErrorsView;
import byransha.graph.view.InNavigationView;
import byransha.graph.view.JumpTo;
import byransha.graph.view.KishanView;
import byransha.graph.view.NodeView;
import byransha.graph.view.OutNavigationView;
import byransha.graph.view.SmallInfoView;
import byransha.nodes.system.User;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import toools.Stop;
import toools.gui.Utilities;

public abstract class BNode {
	static {
		NodeAction.add(BNode.class, Export.class);
		NodeAction.add(BNode.class, Reset.class);
		NodeAction.add(BNode.class, Delete.class);
		NodeAction.add(BNode.class, Search.class);
		NodeAction.add(BNode.class, SearchText.class);
		NodeAction.add(BNode.class, SearchRegexp.class);
		NodeAction.add(BNode.class, Jump.class);

		NodeView.add(BNode.class, KishanView.class);
		NodeView.add(BNode.class, SmallInfoView.class);
		NodeView.add(BNode.class, JumpTo.class);
		NodeView.add(BNode.class, OutNavigationView.class);
		NodeView.add(BNode.class, InNavigationView.class);
		NodeView.add(BNode.class, ErrorsView.class);
		NodeView.add(BNode.class, AvailableActionsView.class);
		NodeView.add(BNode.class, DebugView.class);
	}

	public final BBGraph g;
	public boolean readOnly;
	private int id;

	protected BNode(BBGraph g) {
		if (g == null) {
			this.g = (BBGraph) this;
		} else {
			this.g = g;
			setID(this.g.nextID());
		}

	}

	public NodeView<BNode> findView(Class<? extends NodeView<BNode>> c) {
		for (var v : views()) {
			if (c.isAssignableFrom(v.getClass())) {
				return v;
			}
		}
		return null;
	}

	public User currentUser() {
		return g.systemNode == null ? null : g.systemNode.getCurrentUser();
	}

	public void delete() {
		computeIns().forEach(inArc -> inArc.source().removeOut(this));
		g.removeNode(id());
	}

	public int sizeOf() {
		return sizeOfFields(this);
	}

	public static int sizeOfObject(Object o) {
		if (o == null) {
			return 0;
		} else if (o instanceof BNode) {
			return 0;
		} else if (o.getClass().isArray()) {
			var componentType = o.getClass().getComponentType();
			var arrayLen = Array.getLength(o);

			if (componentType.isPrimitive()) {
				return arrayLen * butils.ByUtils.sizeOfPrimitive.get(componentType);
			} else {
				int sum = 0;

				for (int i = 0; i < arrayLen; ++i) {
					sum += 4 + sizeOfObject(Array.get(o, i));
				}

				return sum;
			}
		} else if (o instanceof Iterable iter) {
			int sum = 4; // nb of elements

			for (var ce : iter) {
				sum += 4 + sizeOfObject(ce);
			}

			return sum;
		} else if (o instanceof LocalDateTime) {
			return 76;
		} else if (o instanceof Map m) {
			return 4 + sizeOfObject(m.keySet()) + 4 + sizeOfObject(m.values());
		} else {
			return sizeOfFields(o);
		}
	}

	private static int sizeOfFields(Object o) {
		int totalSize = 0;

		for (Class c = o.getClass(); c != null; c = c.getSuperclass()) {
			for (var field : c.getDeclaredFields()) {
				if ((field.getModifiers() & Modifier.STATIC) == 0) { // non static
					var fieldDeclaraionType = field.getType();

					if (fieldDeclaraionType.isPrimitive()) {
						totalSize += butils.ByUtils.sizeOfPrimitive.get(fieldDeclaraionType);
					} else {
						totalSize += 4; // ref size

						try {
							field.setAccessible(true);
							totalSize += sizeOfObject(field.get(o));
						} catch (Throwable err) {
							// throw err instanceof RuntimeException re ? re : new RuntimeException(err);
						}
					}
				}
			}
		}

		return totalSize;

	}

	public void toCSVStreams(List<CSVData> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
		var c = new CSVData();
		c.name = "fields";
		c.data = fieldsToCSV(printHeaders);
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
		ascendSuperClassesUntil(BNode.class, c -> {
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

		ascendSuperClassesUntil(BNode.class, c -> {
			NodeAction.actions.getOrDefault(c, (List<Class>) Collections.EMPTY_LIST).forEach(v -> {
				try {
					var action = (NodeAction) v.getConstructor(BBGraph.class).newInstance(g);

					if (action.wantToBeProposedFor(this)) {
						r.add(action);
					}
				} catch (Throwable err) {
					throw err instanceof RuntimeException re ? re : new IllegalStateException(err);
				}
			});
		});

		return r;
	}

	public List<NodeView<BNode>> views() {
		var r = new ArrayList<NodeView<BNode>>();

		ascendSuperClassesUntil(BNode.class, c -> {
			NodeView.views.getOrDefault(c, (List<Class>) Collections.EMPTY_LIST).forEach(v -> {
				try {
					r.add((NodeView) v.getConstructor(BBGraph.class, c).newInstance(g, this));
				} catch (Throwable err) {
					throw err instanceof RuntimeException re ? re : new IllegalStateException(err);
				}
			});
		});

		return r;
	}

	public void ascendSuperClassesUntil(Class<? extends BNode> until, Consumer<Class<? extends BNode>> consumer) {
		for (Class c = getClass(); c != until; c = c.getSuperclass()) {
			consumer.accept(c);
		}

		consumer.accept(until);
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
		synchronized (g) {
			this.id = newID;
			g.setID(this, newID);
		}
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

	public static class BFSResult {
		public Object2IntOpenHashMap<BNode> distances = new Object2IntOpenHashMap<>();
		public Set<BNode> visited = new HashSet<>();
	}

	public BFSResult bfs(int maxDistance, Predicate<BNode> nodeFilter, ObjIntConsumer<BNode> consumer) {
		List<BNode> q = new ArrayList<>();
		var r = new BFSResult();

		BNode c = this;
		q.add(c);
		r.distances.put(c, 0);

		while (!q.isEmpty()) {
			c = q.removeFirst();
			int d = r.distances.getInt(c);

			if (d > maxDistance) { // went too far
				break;
			}

			if (d > 0 && consumer != null) { // don't expose source node
				consumer.accept(c, d);
			}

			final var c_tmp = c;
			c.forEachOut((f, n) -> {
				if (!r.visited.contains(n)) {
					r.visited.add(n);

					if (nodeFilter.test(c_tmp)) {
						q.add(n);
						r.distances.put(n, d + 1);
					}
				}
			});
		}

		return r;
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

	public Color getColor() {
		return Color.white;
	}

	public Icon getIcon() {
		var bytes = getIconBytes();
		return bytes == null ? null : new ImageIcon();
	}

	public byte[] getIconBytes() {
		return null;
	}

	public abstract String prettyName();

	public boolean isReadOnly() {
		return readOnly;
	}

	public final List<NodeError> errors() {
		var errs = new ArrayList<NodeError>();
		fillErrors(errs);
		return errs;
	}

	protected void fillErrors(List<NodeError> errs) {
	}

	final public static JsonNodeFactory factory = new JsonNodeFactory(true);

	public ObjectNode toJSONNode() {
		return toJSONNode(1);
	}

	public ObjectNode toJSONNode(int depth) {
		if (depth < 0)
			return null;

		ObjectNode r = new ObjectNode(factory);
		r.put("id", id());
		r.put("class", getClass().getName());
		r.put("color", Utilities.toRGBHex(getColor()));
		r.put("prettyName", prettyName());

		var iconBytes = getIconBytes();

		if (iconBytes != null) {
			r.put("icon", Base64.getEncoder().encode(getIconBytes()));
		}

		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(currentUser()));
		r.put("canEdit", canEdit(currentUser()));
		r.set("actions",
				new ArrayNode(null, actions().stream().map(e -> (JsonNode) new TextNode(e.commandName())).toList()));
		r.set("errors", new ArrayNode(null, errors().stream().map(err -> (JsonNode) new TextNode(err.msg)).toList()));
		r.set("views", new ArrayNode(null, views().stream().map(v -> (JsonNode) new TextNode(v.name())).toList()));

		var outsNode = new ObjectNode(factory);
		forEachOut((name, out) -> {
			outsNode.put(name, out.toJSONNode(depth - 1));
		});
		r.set("outs", outsNode);

		return r;
	}

}
