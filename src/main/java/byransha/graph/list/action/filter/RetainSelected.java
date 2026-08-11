package byransha.graph.list.action.filter;

import byransha.graph.Element;
import byransha.graph.list.action.ListNode;

public class RetainSelected<N extends Element> extends ListFilter<N> {

	public RetainSelected(ListNode<N> inputNode) {
		super(inputNode);
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