package byransha.graph.action.list.filter;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.nodes.system.ChatNode;

public abstract class FilterNode<N extends BNode> extends NodeAction<ListNode<N>, ListNode<N>> {

	public FilterNode(BGraph g, ListNode<N> inputNode, String cat) {
		super(g, inputNode,  cat);
	}

	@Override
	public final String whatItDoes() {
		return "retain only" + retainsOnly();
	}

	public abstract String retainsOnly();

	@Override
	public ActionResult<ListNode<N>, ListNode<N>> exec(ChatNode chat) throws Throwable {
		var r = new ListNode<N>(g, retainsOnly());

		inputNode.get().forEach(n -> {
			if (retains(n)) {
				r.get().add(n);
			}
		});

		return createResultNode(r, true);
	}

	public abstract boolean retains(N n);

	@Override
	public abstract boolean applies(ChatNode chat);

}