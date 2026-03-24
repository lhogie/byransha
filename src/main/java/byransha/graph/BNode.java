package byransha.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.ai.QueryIA;
import byransha.graph.action.Delete;
import byransha.graph.action.Export;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.Reset;
import byransha.graph.action.list.ListNode;
import byransha.graph.action.search.Search;
import byransha.graph.relection.ClassNode;
import byransha.graph.view.DebugView;
import byransha.graph.view.ErrorsView;
import byransha.graph.view.JumpToMe;
import byransha.graph.view.KishanView;
import byransha.graph.view.NodeView;
import byransha.graph.view.SmallInfoView;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.ui.swing.ColorPalette;
import byransha.ui.swing.TranslatableButton;
import byransha.util.Base62;
import byransha.util.ByUtils;
import byransha.util.TriConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public abstract class BNode {
	@Hide
	public final BGraph g;
	public boolean readOnly;
	public int id = -1;

	public static interface NodeChangeListener {
		void changed(BNode n);
	}

	public List<NodeChangeListener> changeListeners = new ArrayList<>();

	@Hide
	protected ListNode<NodeView> cachedViews;

	@Hide
	protected ListNode<NodeAction> cachedActions;

	protected BNode(BGraph g) {
		if (g == null) {
			this.g = (BGraph) this;
		} else {
			this.g = g;
			this.g.indexes.add(this);
		}
	}

	public BNode error(Throwable err) {
		return error(err, true);
	}

	protected BNode error(Throwable err, boolean rethrow) {
		g.errorLog.add(err);

		if (rethrow) {
			throw err instanceof RuntimeException re ? re : new RuntimeException(err);
		} else {
			err.printStackTrace();
			return g.errorLog;
		}
	}

	public List<NodeView<BNode>> views() {
		if (cachedViews == null) {
			cachedViews = new ListNode(g, "views for node " + this);
			createViews();

			if (findView(JumpToMe.class) == null)
				error(new IllegalStateException("no jump view found for node " + getClass().getName()));
		}

		return (List<NodeView<BNode>>) (List) cachedViews.get();
	}

	public List<NodeAction> actions() {
		if (cachedActions == null) {
			cachedActions = new ListNode<>(g, "actions for node " + this);
			createActions();
		}

		return (List<NodeAction>) cachedActions.get();
	}

	public void invalidateCache() {
		cachedViews = null;
		cachedActions = null;
	}

	public NodeView findView(Class<? extends NodeView> c) {
		for (var v : views()) {
			if (c.isAssignableFrom(v.getClass())) {
				return v;
			}
		}

		return null;
	}

	public User currentUser() {
		return g == null ? null : g.getCurrentUser();
	}

	public void delete() {
		g.indexes.delete(this);
	}

	public int sizeOf() {
		return ByUtils.sizeOfFields(this);
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
		var s = sw.toString();
		pw.close();
		return s;
	}

	public void fieldsToCSV(PrintWriter ps, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
		var fields = new ArrayList<Field>();

		if (printHeaders) {
			forEachOutInFields(getClass(), BNode.class, (f, o, ro) -> fields.add(f));
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
		forEachOutInFields(getClass(), BNode.class, (f, o, ro) -> {
			try {
				if (o == out) {
					f.set(this, null);
				}
			} catch (IllegalAccessException err) {
				error(err);
			}
		});
	}

	public void forEachOutInFields(Class<? extends BNode> from, Class<? extends BNode> until,
			TriConsumer<Field, BNode, Boolean> consumer) {
		ascendSuperClassesUntil(from, until, c -> {
			for (var f : c.getDeclaredFields()) {
				if (BNode.class.isAssignableFrom(f.getType()) && !f.isAnnotationPresent(Hide.class)) {
					try {
						f.setAccessible(true);
						var outNode = (BNode) f.get(this);

						if (outNode != null) {
							var isFinal = (f.getModifiers() & Modifier.FINAL) != 0;
							consumer.accept(f, outNode, isFinal);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						error(e);
					}
				}
			}
		});
	}

	public void forEachOut(BiConsumer<BNode, String> consumer) {
		forEachOutInFields(getClass(), BNode.class, (f, o, ro) -> consumer.accept(o, f.getName()));
	}

	public void forEachOut(Consumer<BNode> consumer) {
		forEachOutInFields(getClass(), BNode.class, (f, o, ro) -> consumer.accept(o));
	}

	public void createActions() {
		cachedActions.elements.add(new QueryIA(g, this));
//		cachedActions.add(new Back(g, this));
		cachedActions.elements.add(new Reset(g, this));
		cachedActions.elements.add(new Export(g, this));
		cachedActions.elements.add(new Delete(g, this));
		cachedActions.elements.add(new Search(g, this));
//		cachedActions.elements.add(new SearchText(g, this));
//		cachedActions.elements.add(new SearchRegexp(g, this));
//		cachedActions.elements.add(new OpenInNewChat(g, this));
	}

	public void createViews() {
		cachedViews.elements.add(new KishanView(this));
		cachedViews.elements.add(new SmallInfoView(g, this));
		cachedViews.elements.add(new JumpToMe(g, this));
//		cachedViews.elements.add(new OutNavigationView(g, this));
//		cachedViews.elements.add(new InNavigationView(g, this));
		cachedViews.elements.add(new ErrorsView(g, this));
		cachedViews.elements.add(new DebugView(g, this));
	}

	public void ascendSuperClassesUntil(Class<? extends BNode> from, Class<? extends BNode> until,
			Consumer<Class<? extends BNode>> consumer) {

		if (!until.isAssignableFrom(from))
			throw new IllegalArgumentException("from " + from + " to " + until);

		for (Class c = from; c != until; c = c.getSuperclass()) {
			consumer.accept(c);
		}

		consumer.accept(until);
	}

	public abstract String whatIsThis();

	public static class BFSResult {
		public Object2IntOpenHashMap<BNode> distances = new Object2IntOpenHashMap<>();
		public Set<BNode> visited = new HashSet<>();
	}

	public BFSResult bfs(long maxDistance, Predicate<BNode> nodeFilter, ObjIntConsumer<BNode> consumer) {
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
			c.forEachOut(o -> {
				if (!r.visited.contains(o)) {
					r.visited.add(o);

					if (nodeFilter.test(c_tmp)) {
						q.add(o);
						r.distances.put(o, d + 1);
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

	public final Color getColor() {
		return ColorPalette.forClass(getClass(), g.ui.colorStyle.style);
	}

	public final Color getBackgroundColor() {
		var c = getColor();
		int alpha = (int) g.ui.transparencyForNodeBackground.get().longValue();
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
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

	public ObjectNode describeAsJSON() {
		return toJSONNode(1);
	}

	public ObjectNode toJSONNode(int depth) {
		if (depth < 0)
			return null;

		ObjectNode r = new ObjectNode(factory);
		r.put("id", id());
		r.put("class", getClass().getName());
		r.put("color", ByUtils.toHex(getColor()));
		r.put("prettyName", prettyName());

		var iconBytes = getIconBytes();

		if (iconBytes != null) {
			r.put("icon", Base64.getEncoder().encode(getIconBytes()));
		}

		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(currentUser()));
		r.put("canEdit", canEdit(currentUser()));
		r.set("actions", new ArrayNode(null, actions().stream().map(e -> (JsonNode) new IntNode(e.id())).toList()));
		r.set("errors", new ArrayNode(null, errors().stream().map(err -> (JsonNode) new TextNode(err.msg)).toList()));
		r.set("views", new ArrayNode(null, views().stream().map(v -> (JsonNode) new TextNode(v.id() + "")).toList()));

		var outsNode = new ObjectNode(factory);
		forEachOutInFields(getClass(), BNode.class, (f, out, ro) -> outsNode.put(f.getName(), out.id()));
		r.set("outs", outsNode);

		return r;
	}

	public NodeAction findAction(String actionName) {
		for (var a : actions()) {
			if (a.technicalName().equals(actionName)) {
				return a;
			}
		}

		return null;
	}

	public NodeView<BNode> getViewForKishanView() {
		for (var v : views()) {
			if (!(v instanceof KishanView)) {
				return v;
			}
		}

		throw new IllegalStateException("no kishanable view found for node " + this);
	}

	public void reset() {
		forEachOutInFields(getClass(), BNode.class, (f, o, ro) -> {
			if (!ro) {
				try {
					var v = (BNode) f.get(this);

					if (v instanceof ValuedNode) {
						v.reset();
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public JButton createJumpButton(ChatNode chat) {
		var b = new TranslatableButton(chat.g.translator);
		b.setText(prettyName());

//		b.setContentAreaFilled(true);
		b.setPreferredSize(new Dimension(100, 30));
		b.addActionListener(e -> chat.append(this));
		b.setToolTipText(whatIsThis());
		b.setFocusable(false);

		return b;
	}

	public JButton createJumpButton2(ChatNode chat) {
		var b = new JButton(prettyName()) {
			@Override
			public Color getBackground() {
				return getColor();
			}
		};
//		b.setContentAreaFilled(true);
		b.setPreferredSize(new Dimension(100, 30));
		b.addActionListener(e -> chat.append(this));
		b.setToolTipText(whatIsThis());
		return b;
	}

	public String idAsText() {
		return Base62.encode(id());
	}

	public <N extends BNode> ClassNode getClassNode() {
		for (ClassNode c : (Collection<ClassNode>) (Collection) g.indexes.byClass.m.get(ClassNode.class)) {
			if (c.clazz == getClass()) {
				return c;
			}
		}

		throw new IllegalStateException("class node should be registered");
	}

	public void set(Field f, BNode newValue) throws IllegalArgumentException, IllegalAccessException {
		f.set(this, newValue);
	}

	public String t(String s) {
		var translation = g.translator.translate(s);
		return translation == null ? s : translation;
	}

}
