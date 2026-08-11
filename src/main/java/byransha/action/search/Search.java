package byransha.action.search;

import byransha.Element;
import byransha.action.Category;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;
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