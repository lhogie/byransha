package byransha.graph.action.search;

import byransha.graph.ShowInKishanView;
import byransha.graph.Element;
import byransha.graph.Category;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.primitive.LongNode;
import byransha.primitive.LongNode.Bounds;

public class Search extends FunctionAction<Element, ListNode> {
	@ShowInKishanView
	public LongNode depth;

	public static class search extends Category {
	}

	public Search(Element src) {
		super(src, search.class);
		depth = new LongNode(this, null);
		depth.set(1L);
		depth.setBounds(new Bounds(0, 20));// src.computeLongestPathLength()));
	}

	@Override
	public void impl() {
		var list = new ListNode<>(this, null, "search result at depth " + depth, Element.class);
		inputNode.bfs(depth.get(), n -> accept(n), (n, depth) -> list.elements.add(n));
		result = list;
	}

	protected boolean accept(Element n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "search all nodes until a given distance";
	}

	@Override
	public boolean applies() {
		return true;
	}

}