package byransha.graph.list.action.filter;

import byransha.graph.BNode;
import byransha.graph.Category.list;
import byransha.graph.Category.selection;
import byransha.graph.list.action.ListNode;

public class RetainSelected<N extends BNode> extends FilterNode<N> {

	public RetainSelected(ListNode<N> inputNode) {
		super(inputNode, list.class, selection.class);
	}

	@Override
	public String retainsOnly() {
		return "selected nodes";
	}

	@Override
	public boolean retains(N n) {
		return inputNode.isSelected(n);
	}

	@Override
	public boolean applies() {
		return inputNode.elements.size() > 0;
	}
}