package byransha.graph.action.list;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.Export.CSVData;
import byransha.graph.action.list.filter.RetainSelected;
import byransha.graph.view.DotAction;
import byransha.graph.view.GeneratePlantUML;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.ListNodeView;
import byransha.nodes.system.ChatNode;
import byransha.util.IntObjectBiConsumer;
import byransha.util.ListenableList;

public final class ListNode<T extends BNode> extends BNode {
	String label;
	final public ListenableList<T> elements = new ListenableList<>();
	final public ListenableList<T> selection = new ListenableList<>();

	public ListNode(BGraph g, String label) {
		super(g);
		this.label = label;
	}

	public void selectAll() {
		selection.clear();
		selection.addAll(elements);
	}

	public void selectNone() {
		selection.clear();
	}

	public void invertSelection() {
		for (T e : elements) {
			if (selection.contains(e)) {
				selection.remove(e);
			} else {
				selection.add(e);
			}
		}
	}

	public void shuffle() {
		Collections.shuffle(elements);
	}

	@Override
	public void forEachOut(BiConsumer<BNode, String> consumer) {
		super.forEachOut(consumer);
		forEachOutInContent((i, o) -> consumer.accept(o, "" + i));
	}

	public void forEachOutInContent(IntObjectBiConsumer<BNode> consumer) {
		var l = elements;

		for (int i = 0; i < l.size(); ++i) {
			consumer.accept(i, l.get(i));
		}
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new ListNodeView(g, this));
		super.createViews();
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new EDistribution(g, this));
		cachedActions.elements.add(new RetainSelected<>(g, this));
		cachedActions.elements.add(new NodeAction<ListNode, ListNode>(g, this, "") {

			@Override
			public String whatItDoes() {
				return "select all";
			}

			@Override
			public ActionResult<ListNode, ListNode> exec(ChatNode chat) throws Throwable {
				inputNode.selectAll();
				return createResultNode(null, true);
			}

			@Override
			public boolean applies(ChatNode chat) {
				return inputNode.selection.size() < inputNode.elements.size();
			}
		});
		cachedActions.elements.add(new NodeAction<ListNode, ListNode>(g, this, "") {

			@Override
			public String whatItDoes() {
				return "invert selection";
			}

			@Override
			public ActionResult<ListNode, ListNode> exec(ChatNode chat) throws Throwable {
				inputNode.invertSelection();
				return createResultNode(null, true);
			}

			@Override
			public boolean applies(ChatNode chat) {
				return true;
			}
		});
		cachedActions.elements.add(new NodeAction<ListNode, ListNode>(g, this, "") {

			@Override
			public String whatItDoes() {
				return "shuffle";
			}

			@Override
			public ActionResult<ListNode, ListNode> exec(ChatNode chat) throws Throwable {
				inputNode.shuffle();
				return createResultNode(null, true);
			}

			@Override
			public boolean applies(ChatNode chat) {
				return true;
			}
		});

		cachedActions.elements.add(new DotAction(g, this));
		cachedActions.elements.add(new GeneratePlantUML(g, this));
		super.createActions();
	}

	@Override
	public void removeOut(BNode out) {
		elements.remove(out);
	}

	@Override
	public void toCSVStreams(List<CSVData> l, boolean printHeaders)
			throws IllegalArgumentException, IllegalAccessException {

		for (int i = 0; i < elements.size(); ++i) {
			elements.get(i).fieldsToCSV(i == 0 ? printHeaders : false);
		}
	}

	public boolean isHeterogeneous() {
		return classes().size() <= 1;
	}

	public MultiValuedMap<Class<? extends BNode>, BNode> classes() {
		var r = new HashSetValuedHashMap<Class<? extends BNode>, BNode>();
		elements.forEach(e -> r.put(e.getClass(), e));
		return r;
	}

	@Override
	public String whatIsThis() {
		return "a list of nodes";
	}

	@Override
	public String toString() {
		return label == null ? "a list" : label;
	}

	public void select(int i) {
		selection.add(elements.get(i));
	}

	public void unselect(int i) {
		selection.remove(elements.get(i));
	}

	public List<T> getSelected() {
		return selection;
	}

	public void reset() {
		super.reset();
		elements.clear();
	}

	public String toDot() {
		var s = new StringWriter();
		var pw = new PrintWriter(s);
		pw.println("digraph {");
		elements.forEach(e -> pw.println(e.id() + ";"));
		elements.forEach(
				e -> e.forEachOut((o, role) -> pw.println(e.id() + " -> " + o.id() + "[label=\"" + role + "\"]")));
		pw.println("}");
		return s.toString();
	}

	public List<T> get() {
		return elements;
	}

	public void set(List<T> l) {
		elements.clear();
		selection.clear();
		elements.addAll(l);
	}

	public boolean isSelected(T n) {
		return selection.contains(n);
	}

	public static class EDistribution<V extends BNode> extends NodeAction<ListNode<V>, DistributionNode<V>> {

		public EDistribution(BGraph g, ListNode<V> inputNode) {
			super(g, inputNode, "list/statistics");
		}

		@Override
		public String whatItDoes() {
			return "computes distribution";
		}

		@Override
		public ActionResult<ListNode<V>, DistributionNode<V>> exec(ChatNode chat) throws Throwable {
			var d = new DistributionNode<V>(g) {

				@Override
				public String toString() {
					return inputNode.label;
				}
			};
			inputNode.get().forEach(e -> d.entries.addOccurence(e, 1));
			return createResultNode(d, readOnly);
		}

		@Override
		public boolean applies(ChatNode chat) {
			return true;
		}

	}

}
