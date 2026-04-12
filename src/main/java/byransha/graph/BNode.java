package byransha.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.ai.QueryIA;
import byransha.graph.action.Delete;
import byransha.graph.action.Export;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.FreezingAction;
import byransha.graph.action.JumpToAnotherNode;
import byransha.graph.action.Reset;
import byransha.graph.action.search.Search;
import byransha.graph.action.search.SearchRegexp;
import byransha.graph.action.search.SearchText;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.lab.DynamicValuedNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.CircleComponent;
import byransha.ui.swing.ColorPalette;
import byransha.ui.swing.ErrorIndicator;
import byransha.ui.swing.MenuBuilder;
import byransha.ui.swing.TextDisplayComponent;
import byransha.ui.swing.TranslatableButton;
import byransha.ui.swing.TranslatableTextArea;
import byransha.ui.swing.Utils;
import byransha.ui.swing.WrapPanel;
import byransha.util.Base62;
import byransha.util.ByUtils;
import byransha.util.Stop;
import byransha.util.TriConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public abstract class BNode {
	@DoNotShowOnChat
	public final BGraph g;
	public boolean readOnly;
	public long id = -1;

	public static class node extends Category {
	}

	@DoNotShowOnChat
	protected ListNode<Action> cachedActions;

	protected BNode(BGraph g) {
		if (g == null) {
			this.g = (BGraph) this;
		} else {
			this.g = g;
			this.g.indexes.add(this);
		}
	}

	public int computeLongestPathLength() {
		var r = bfs(Long.MAX_VALUE, n -> true, (n, d) -> {
		});
		return r.longestDistance();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " #" + idAsText();
	}

	public BNode error(Throwable err) {
		return error(err, true);
	}

	public BNode error(Throwable err, boolean rethrow) {
		g.errorLog.add(err);

		if (rethrow) {
			throw err instanceof RuntimeException re ? re : new RuntimeException(err);
		} else {
			err.printStackTrace();
			return g.errorLog;
		}
	}

	public List<Action> actions() {
		if (cachedActions == null) {
			cachedActions = new ListNode<>(g, "actions for node " + this);
			createActions();
		}

		return (List<Action>) cachedActions.get();
	}

	public void invalidateCache() {
		cachedActions = null;
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
				if (BNode.class.isAssignableFrom(f.getType()) && !f.isAnnotationPresent(DoNotShowOnChat.class)) {
					try {
						f.setAccessible(true);
						var outNode = (BNode) f.get(this);

						var isFinal = (f.getModifiers() & Modifier.FINAL) != 0;
						consumer.accept(f, outNode, isFinal);
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

//		cachedActions.add(new Back(g, this));
		cachedActions.elements.add(new QueryIA(this));
		cachedActions.elements.add(new SeeClassNode(this));
		cachedActions.elements.add(new CopyIDToClipboard(this));
		cachedActions.elements.add(new FreezingAction(g));
		cachedActions.elements.add(new JumpToAnotherNode(g));
		cachedActions.elements.add(new Reset(this));
		cachedActions.elements.add(new Export(this));
		cachedActions.elements.add(new Delete(this));
		cachedActions.elements.add(new Search(this));
		cachedActions.elements.add(new SearchText(this));
		cachedActions.elements.add(new SearchRegexp(this));
		cachedActions.elements.add(new OpenInNewChat(this));
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

	public String whatIsThis() {
		return getClassNode().whatItRepresents();
	}

	public static class BFSResult {
		public Object2IntOpenHashMap<BNode> distances = new Object2IntOpenHashMap<>();
		public Set<BNode> visited = new HashSet<>();

		public int longestDistance() {
			int max = 0;
			for (int d : distances.values()) {
				if (d > max) {
					max = d;
				}
			}
			return max;
		}
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
				if (o != null && !r.visited.contains(o)) {
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

	public final long id() {
		return id;
	}

	@Override
	public final int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}

	public final Color getColor() {
		return ColorPalette.forClass(getClass(), g.swing.colorStyle.style);
	}

	public final Color getBackgroundColor() {
		var c = getColor();
		int alpha = (int) g.swing.transparencyForNodeBackground.get().longValue();
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	public Icon getIcon() {
		var bytes = getIconBytes();
		return bytes == null ? null : new ImageIcon();
	}

	public byte[] getIconBytes() {
		return null;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public final List<NodeError> errors() {
		var errs = new ArrayList<NodeError>();
		fillErrors(errs);
		return errs;
	}

	protected void fillErrors(List<NodeError> errs) {
		forEachOutInFields(getClass(), BNode.class, (f, v, ro) -> {
			if (v instanceof ValuedNode vn) {
				errs.addAll(vn.errors());
			}
		});
	}

	final public static JsonNodeFactory factory = new JsonNodeFactory(true);

	public ObjectNode describeAsJSON() {
		return toJSONNode(1);
	}

	public ObjectNode toJSONNode(int depth) {
		if (depth < 0)
			return null;

		ObjectNode r = new ObjectNode(factory);
		r.put("id", idAsText());
		r.put("class", getClass().getName());
		r.put("color", ByUtils.toHex(getColor()));
		r.put("toString", toString());

		var iconBytes = getIconBytes();

		if (iconBytes != null) {
			r.put("icon", Base64.getEncoder().encode(getIconBytes()));
		}

		r.put("whatIsThis", whatIsThis());
		r.put("canSee", canSee(currentUser()));
		r.put("canEdit", canEdit(currentUser()));
		r.set("actions",
				new ArrayNode(null, actions().stream().map(e -> (JsonNode) new TextNode(e.idAsText())).toList()));
		r.set("errors", new ArrayNode(null, errors().stream().map(err -> (JsonNode) new TextNode(err.msg)).toList()));

		var outsNode = new ObjectNode(factory);
		forEachOutInFields(getClass(), BNode.class,
				(f, out, ro) -> outsNode.put(f.getName(), out != null ? out.idAsText() : ""));
		r.set("outs", outsNode);

		return r;
	}

	public User currentUser() {
		return g.getCurrentUser();
	}

	public Action findAction(String actionName) {
		for (var a : actions()) {
			if (a.technicalName().equals(actionName)) {
				return a;
			}
		}

		return null;
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
		b.setText(toString());

//		b.setContentAreaFilled(true);
		b.setPreferredSize(new Dimension(100, 30));
		b.addActionListener(e -> chat.append(this));
		b.setToolTipText(whatIsThis());
		b.setFocusable(false);

		return b;
	}

	public JButton createJumpButton2(ChatNode chat) {
		var b = new JButton(toString()) {
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
			if (c.representedClass == getClass()) {
				return c;
			}
		}

		throw new IllegalStateException("class node should be registered: " + getClass());
	}

	public void set(Field f, BNode newValue) throws IllegalArgumentException, IllegalAccessException {
		f.set(this, newValue);
	}

	public String t(String s) {
		var translation = g.translator.translate(s);
		return translation == null ? s : translation;
	}

	public void writeTo(ChatSheet sheet) {
		int fieldNameSize = fieldMaxLenght();

		forEachOutInFields(getClass(), BNode.class, (f, out, readOnly) -> {
			if (out != this) {
				if (out instanceof DynamicValuedNode otf) {
					out = otf.exec();
				}

				fillLine(sheet.currentLine, f, sheet, out, fieldNameSize);
				sheet.newLine();
			}
		});
	}

	public int fieldMaxLenght() {
		int[] max = { 0 };
		forEachOutInFields(getClass(), BNode.class, (f, out, readOnly) -> {
			if (out != this)
				max[0] = Math.max(max[0], f.getName().length());
		});
		return max[0];
	}

	private void fillLine(WrapPanel currentLine, Field f, ChatSheet sheet, BNode out, int left) {
		var fieldNameComponent = new TextDisplayComponent(g.translator, f.getName() + ":");
		fieldNameComponent.setColumns(left);
		fieldNameComponent.setToolTipText(f.getName());
		Utils.idDropTarget(g, fieldNameComponent, dn -> set(f, dn));
		currentLine.add(fieldNameComponent);

		if (out != null) {
			currentLine.add(out.createBall(18, 2, ((ChatSheet) sheet).chat));
			currentLine.add(new ErrorIndicator(out));
			out.writeTo(sheet);
		} else {
			sheet.appendToCurrentLine("field has no value");
		}

		{
			var popup = new JPopupMenu();
			var setToNull = new JMenuItem("unset");
			setToNull.addActionListener(e -> {
				try {
					set(f, null);
					sheet.currentLine.removeAll();
					fillLine(currentLine, f, sheet, out, left);
					sheet.doLayout();
					sheet.revalidate();
				} catch (Throwable e1) {
					error(e1);
				}
			});
			var replace = new JMenuItem("see candidates");
			replace.addActionListener(e -> {
				var list = new ListNode(g, "all nodes of class " + f.getType().getName());
				g.indexes.byClass.forEachNodeAssignableTo((Class) f.getType(), a -> {
					list.elements.add(a);
					return Stop.no;
				});
				new ChatNode(currentUser()).append(list);
			});

			if (!this.readOnly) {
				popup.add(out == null ? replace : setToNull);
			}

			fieldNameComponent.setComponentPopupMenu(popup);
		}
	}

	public JComponent createBall(int diameter, int border, ChatNode chat) {
		var c = new CircleComponent(diameter, getColor());
		c.setBorderWidth(border);
		c.setOpaque(false);
		c.setFocusable(false);
		var tooltip = "<html>" + whatIsThis() + "<br><ul><li>" + idAsText() + "</ul></html>";
		c.setToolTipText(tooltip);
//		SelectableTooltip.addSelectableTooltip(c,tooltip);
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_COPY,
				e -> e.startDrag(DragSource.DefaultCopyDrop, new StringSelection(idAsText())));

		c.setComponentPopupMenu(MenuBuilder.buildPopupMenu(actions(), chat));

		c.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					chat.append(BNode.this);
				}
			}
		});

		return c;
	}

	public JComponent getListItemComponent(ChatNode chat) {
		var p = new JPanel();
		p.setOpaque(false);
		p.add(createBall(20, 15, chat));
		var ta = new TranslatableTextArea(chat.g.translator);
		ta.setToolTipText(whatIsThis());
		ta.setText(toString());
		p.add(ta);

		for (var c : p.getComponents()) {
//			((JComponent) c).setBorder(LineBorder.createBlackLineBorder());
		}
		return p;
	}
}
