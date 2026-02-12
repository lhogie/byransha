package byransha.nodes.primitive;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.system.User;

public class ListNode<T extends BNode> extends ValuedNode<List<T>> {
	String label;

	public ListNode(BBGraph db, User creator) {
		super(db, creator);
		set(new ArrayList<>(), creator);
	}

	@Override
	public void removeOut(BNode out, User user) {
		var l = get();
		l.remove(out);
	}

	@Override
	public void toCSV(PrintWriter ps, boolean printHeaders) throws IllegalArgumentException, IllegalAccessException {
		var l = get();

		for (int i = 0; i < l.size(); ++i) {
			var n = l.get(i);
			n.toCSV(ps, i == 0 ? printHeaders : false);
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
	protected List<T> bytesToValue(byte[] bytes, User user) throws IOException {
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

	public void add(T element, User user) {
		List<T> newL = new ArrayList<>(get());
		newL.add(element);
		set(newL, user);
	}

	public void remove(T element) {
		get().remove(element);
	}

	public String getSelected() {
		if (get().isEmpty()) {
			return null;
		}

		return get().stream().filter(e -> e instanceof StringNode).map(e -> (StringNode) e).map(StringNode::get)
				.findFirst().orElse(null);
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

}
