package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ListNode;

public class PruneList extends NodeAction<ListNode, ListNode> {

	public PruneList(BGraph g, ListNode inputNode) {
		super(g, inputNode);
		this.execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "retain selection only";
	}

	@Override
	public ActionResult<ListNode, ListNode> exec() throws Throwable {
		// TODO Auto-generated method stub
		var r = new ListNode<>(g, "pruned list");
		r.get().addAll(inputNode.getSelected());
		return createResultNode(r, true);
	}

}