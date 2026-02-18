package byransha.nodes.primitive;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.BNode.exportNodeAction.CSVStream;
import byransha.graph.NodeView;

public class ListNode<T extends BNode> extends ValuedNode<List<T>> {
	String label;
	private List<T> selected = new ArrayList<>();

	public ListNode(BBGraph g) {
		super(g);
		set(new ArrayList<>());
	}

	@Override
	public void removeOut(BNode out) {
		var l = get();
		l.remove(out);
	}

	@Override
	public void toCSVStreams(List<CSVStream> l, boolean printHeaders)
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

		return new String(bytes).lines().map(l -> (T) g.findByID(Integer.valueOf(l))).toList();
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
		return label;
	}

	@Override
	public void forEachOut(BiConsumer<String, BNode> consumer) {
		super.forEachOut(consumer);

		for (T element : getElements()) {
			if (element instanceof BNode bNode) {
				consumer.accept(bNode.id() + ". " + bNode.prettyName(), bNode);
			}
		}
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

	public static class ElementView extends NodeView<ListNode<BNode>> {

		public ElementView(BBGraph g) {
			super(g);
		}

		@Override
		public JsonNode toJSON(ListNode<BNode> n) {
			var r = new ArrayNode(null);
			n.get().forEach(e -> r.add(e.toJSONNode(0)));
			return r;
		}

		@Override
		public JComponent createComponentImpl(ListNode<BNode> n) {
			var jlist = new JList();
			jlist.setListData(n.get().toArray());
			jlist.setCellRenderer(new ListCellRenderer<BNode>() {

				@Override
				public Component getListCellRendererComponent(JList<? extends BNode> list, BNode value, int index,
						boolean isSelected, boolean cellHasFocus) {
					return value.views().getFirst().createComponent(value);
				}
			});

			return jlist;
		}

	}

}
