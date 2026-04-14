package byransha.graph;

import byransha.graph.BGraph.graph;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.util.Stop;

public class AllNodes extends FunctionAction<BGraph, ListNode<BNode>> {

	public AllNodes(BGraph g) {
		super(g, graph.class);
	}

	@Override
	public String whatItDoes() {
		return "list of nodes in the graph";
	}

	@Override
	public void impl() throws Throwable {
		result = new ListNode<>(g, "all nodes in the graph", BNode.class);
		g.indexes.nodesList.forEachNode(n -> {
			result.get().add(n);
			return Stop.no;
		});
	}

	@Override
	public boolean applies() {
		return true;
	}

}
