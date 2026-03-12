package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.ChatNode;

public class PruneList<N extends BNode> extends NodeAction<ListNode<N>, ListNode<N>> {

	public PruneList(BGraph g, ListNode<N> inputNode) {
		super(g, inputNode);
		this.execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "retain selection only";
	}

	@Override
	public ActionResult<ListNode<N>, ListNode<N>> exec(ChatNode chat) throws Throwable {
		var r = new ListNode<N>(g, "pruned list");
		r.get().addAll(inputNode.getSelected());
		return createResultNode(r, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return inputNode.getSelected().size() > 2;
	}

}