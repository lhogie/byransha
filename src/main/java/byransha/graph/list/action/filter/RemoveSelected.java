package byransha.graph.list.action.filter;

import byransha.graph.BNode;
import byransha.graph.Category.list;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;

public class RemoveSelected<N extends BNode> extends FunctionAction<ListNode<N>, ListNode<N>> {

	public RemoveSelected(ListNode<N> l) {
		super(l, list.class);
	}

	@Override
	public boolean applies() {
		return true;// inputNode.selection.size() > 0;
	}

	@Override
	public String whatItDoes() {
		return "remove selection";
	}

	@Override
	protected void impl() throws Throwable {
		inputNode.elements.removeAll(inputNode.selection);
		inputNode.selection.clear();
	}
}