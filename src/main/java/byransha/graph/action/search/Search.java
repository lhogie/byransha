package byransha.graph.action.search;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;

public class Search extends NodeAction<BNode, ListNode> {
	public IntNode depth;

	public Search(BBGraph g, BNode src) {
		super(g, src);
		depth = new IntNode(g);
		depth.set(1);
		depth.setBounds(0, 100000);
	}

	@Override
	public ActionResult<BNode, ListNode> exec(BNode target) {
		var list = new ListNode(g);
		target.bfs(depth.get(), n -> accept(n), (n, depth) -> list.add(n));
		return createResultNode(list);
	}

	protected boolean accept(BNode n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "search all nodes until a given distance";
	}
}