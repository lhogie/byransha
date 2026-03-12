package byransha.graph;

import butils.Stop;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.ChatNode;

public class AllNodes extends NodeAction<BGraph, ListNode<BNode>> {

	public AllNodes(BGraph g) {
		super(g, g);
		execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "list of nodes in the graph";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		var r = new ListNode<>(g, "all nodes in the graph");
		g.indexes.nodesList.forEachNode(n -> {
			r.get().add(n);
			return Stop.no;
		});
		return createResultNode(r, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
