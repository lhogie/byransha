package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;

public class SearchAction extends NodeAction<BNode, ListNode> {
	public IntNode depth;

	public SearchAction(BBGraph g) {
		super(g);
		depth = new IntNode(g);
		depth.set(1);
		depth.setBounds(0, 100000);
	}

	@Override
	public ActionResult<BNode, ListNode> exec(BNode target) {
		var list = new ListNode(g);
		target.bfs(depth.get(), n -> accept(n), (n, depth) -> list.add(n));
		return new ActionResult<BNode, ListNode>(g, this, list);
	}

	protected boolean accept(BNode n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "search all nodes until a given distance";
	}
}