package byransha.graph.action.list;

import java.util.HashSet;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode.list;
import byransha.nodes.system.ChatNode;

public class Uniq extends FilterAction<ListNode> {

	public Uniq(BGraph g, ListNode inputNode) {
		super(g, inputNode, list.class);
	}

	@Override
	public String whatItDoes() {
		return "remove duplicates";
	}

	@Override
	public boolean applies(ChatNode chat) {
		return !inputNode.elements.isEmpty();
	}

	@Override
	protected void apply(ListNode node) {
		var s = new HashSet<>(node.elements);
		node.elements.clear();
		node.elements.addAll(s);
	}

}
