package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode.list;
import byransha.nodes.system.ChatNode;

public class Shuffle extends FilterAction<ListNode> {

	public Shuffle(BGraph g, ListNode inputNode) {
		super(g, inputNode, list.class);
	}

	@Override
	protected void apply(ListNode node) {
		node.shuffle();
	}

	@Override
	public String whatItDoes() {
		return "shuffle";
	}

	@Override
	public boolean applies(ChatNode chat) {
		return !inputNode.elements.isEmpty();
	}

}
