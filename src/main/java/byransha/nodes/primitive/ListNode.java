package byransha.nodes.primitive;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import butils.IntObjectBiConsumer;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.PruneList;

public class ListNode<T extends BNode> extends ValuedNode<List<T>> {

	String label;
	List<T> selected = Collections.EMPTY_LIST;

	public ListNode(BGraph g, String label) {
		super(g);
		set(new ArrayList<>());
		this.label = label;
	}

	@Override
	public void forEachOut(BiConsumer<BNode, String> consumer) {
		super.forEachOut(consumer);
		forEachOutInContent((i, o) -> consumer.accept(o, "" + i));
	}

	public void forEachOutInContent(IntObjectBiConsumer<BNode> consumer) {
		var l = get();

		for (int i = 0; i < l.size(); ++i) {
			consumer.accept(i, l.get(i));
		}
	}

	@Override
	public void createViews() {
		// cachedViews.add(new ListNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		cachedActions.add(new PruneList(g, this));
		super.createActions();
	}

	@Override
	public void removeOut(BNode out) {
		var l = get();
		l.remove(out);
	}

	@Override
	public void toCSVStreams(List<CSVData> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {
		var elements = get();

		for (int i = 0; i < elements.size(); ++i) {
			elements.get(i).fieldsToCSV(i == 0 ? printHeaders : false);
		}
	}

	@Override
	public List<T> defaultValue() {
		return new ArrayList<T>();
	}

	public boolean isHeterogeneous() {
		return classes().size() <= 1;
	}

	public Set<Class<? extends BNode>> classes() {
		var r = new HashSet<Class<? extends BNode>>();
		get().forEach(e -> r.add(e.getClass()));
		return r;
	}

	@Override
	protected List<T> bytesToValue(byte[] bytes) throws IOException {
		if (bytes.length == 0) {
			return Collections.emptyList();
		}

		return new String(bytes).lines().map(l -> (T) g.i.byId.get(Integer.valueOf(l))).toList();
	}

	@Override
	protected byte[] valueToBytes(List<T> ts) throws IOException {
		if (ts.isEmpty()) {
			return new byte[0];
		}

		StringBuilder s = new StringBuilder();
		for (T element : ts) {
			s.append(element.id()).append('\n');
		}
		return s.toString().getBytes();
	}

	@Override
	public String whatIsThis() {
		return "a list of nodes";
	}

	@Override
	public String prettyName() {
		return label == null ? "a list" : label;
	}

	public void add(T element) {
		List<T> newL = new ArrayList<>(get());
		newL.add(element);
		set(newL);
	}

	public void remove(T element) {
		get().remove(element);
	}

	public void select(T element) {
		selected.add(element);
	}

	public void unselect(T element) {
		selected.remove(element);
	}

	public List<T> getSelected() {
		return selected;
	}

	public void removeAll() {
		get().clear();
	}

	public List<T> getElements() {
		if (get() == null) {
			return Collections.emptyList();
		}

		return List.copyOf(get());
	}

	public int size() {
		return get().size();
	}

	public T get(int index) {
		if (index < 0 || index >= get().size()) {
			return null;
		}
		return get().stream().skip(index).findFirst().orElse(null);
	}

	public String toDot() {
		var s = new StringWriter();
		var pw = new PrintWriter(s);
		pw.println("digraph {");
		get().forEach(e -> pw.println(e.id() + ";"));
		get().forEach(e -> e.forEachOut(o -> pw.println(e.id() + " -> " + o.id())));
		pw.println("}");
		return s.toString();
	}

}
