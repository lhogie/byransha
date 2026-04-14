package byransha.graph.list.action.filter;

import byransha.graph.BNode;
import byransha.graph.list.action.ListNode;

public class RemoveSelected<N extends BNode> extends ListFilter<N> {

	public RemoveSelected(ListNode<N> inputNode) {
		super(inputNode);
	}

	@Override
	public String retainsOnly() {
		return "non-selected nodes";
	}

	@Override
	public boolean retains(N n) {
		return !inputNode.isSelected(n);
	}

	@Override
	public boolean applies() {
		return inputNode.elements.size() > 0;
	}
}