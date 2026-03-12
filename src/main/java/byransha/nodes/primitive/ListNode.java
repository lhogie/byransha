package byransha.nodes.primitive;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import butils.IntObjectBiConsumer;
import butils.ListenableList;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.PruneList;
import byransha.graph.view.DotAction;
import byransha.graph.view.GeneratePlantUML;

public class ListNode<T extends BNode> extends BNode {
	String label;
	final public ListenableList<T> values = new ListenableList<>();
	private final ListenableList<T> selection = new ListenableList<>();

	public ListNode(BGraph g, String label) {
		super(g);
		this.label = label;

		var listener = new ListenableList.ChangeListener<T>() {

			@Override
			public void onAdd(T element) {
				changeListeners.forEach(l -> l.changed(ListNode.this));
			}

			@Override
			public void onRemove(T element) {
				changeListeners.forEach(l -> l.changed(ListNode.this));
			}
		};

		values.addChangeListener(listener);
		selection.addChangeListener(listener);
	}

	public T get(int i) {
		return values.get(i);
	}

	@Override
	public void forEachOut(BiConsumer<BNode, String> consumer) {
		super.forEachOut(consumer);
		forEachOutInContent((i, o) -> consumer.accept(o, "" + i));
	}

	public void forEachOutInContent(IntObjectBiConsumer<BNode> consumer) {
		var l = values;

		for (int i = 0; i < l.size(); ++i) {
			consumer.accept(i, l.get(i));
		}
	}

	@Override
	public void createViews() {
		cachedViews.values.add(new ListNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		cachedActions.values.add(new PruneList(g, this));
		cachedActions.values.add(new DotAction(g, this));
		cachedActions.values.add(new GeneratePlantUML(g, this));
		super.createActions();
	}

	@Override
	public void removeOut(BNode out) {
		values.remove(out);
	}

	@Override
	public void toCSVStreams(List<CSVData> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {

		for (int i = 0; i < values.size(); ++i) {
			values.get(i).fieldsToCSV(i == 0 ? printHeaders : false);
		}
	}

	public boolean isHeterogeneous() {
		return classes().size() <= 1;
	}

	public Set<Class<? extends BNode>> classes() {
		var r = new HashSet<Class<? extends BNode>>();
		values.forEach(e -> r.add(e.getClass()));
		return r;
	}

	@Override
	public String whatIsThis() {
		return "a list of nodes";
	}

	@Override
	public String prettyName() {
		return label == null ? "a list" : label;
	}

	public void select(int i) {
		selection.add(values.get(i));
	}

	public void unselect(int i) {
		selection.remove(values.get(i));
	}

	public List<T> getSelected() {
		return selection;
	}

	public void reset() {
		super.reset();
		values.clear();
	}

	public String toDot() {
		var s = new StringWriter();
		var pw = new PrintWriter(s);
		pw.println("digraph {");
		values.forEach(e -> pw.println(e.id() + ";"));
		values.forEach(
				e -> e.forEachOut((o, role) -> pw.println(e.id() + " -> " + o.id() + "[label=\"" + role + "\"]")));
		pw.println("}");
		return s.toString();
	}

	public void add(T n) {
		values.add(n);
	}

	public List<T> get() {
		return values;
	}

	public int size() {
		return values.size();
	}

	public void set(List<T> l) {
		values.clear();
		selection.clear();
		values.addAll(l);
	}

	public List<T> elements() {
		return values;
	}

}
