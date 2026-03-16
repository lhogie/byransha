package byransha.graph.action.search;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.ChatNode;

public class Search extends NodeAction<BNode, ListNode> {
	public LongNode depth;

	public Search(BGraph g, BNode src) {
		super(g, src);
		depth = new LongNode(g);
		depth.set(1L);
		depth.setBounds(0, 100000);
	}

	@Override
	public ActionResult<BNode, ListNode> exec(ChatNode chat) {
		var list = new ListNode(g, "search result at depth " + depth);
		inputNode.bfs(depth.get(), n -> accept(n), (n, depth) -> list.add(n));
		return createResultNode(list, false);
	}

	protected boolean accept(BNode n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "search all nodes until a given distance";
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}