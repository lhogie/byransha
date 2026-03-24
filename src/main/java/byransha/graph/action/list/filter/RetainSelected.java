package byransha.graph.action.list.filter;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.nodes.system.ChatNode;

public class RetainSelected<N extends BNode> extends FilterNode<N> {

	public RetainSelected(BGraph g, ListNode<N> inputNode) {
		super(g, inputNode);
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
	public boolean applies(ChatNode chat) {
		return inputNode.elements.size() > 0;
	}
}