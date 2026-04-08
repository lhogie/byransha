package byransha.graph.action.list.map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.nodes.system.ChatNode;

public abstract class AbstractMapAction<A extends BNode, B extends BNode> extends NodeAction<ListNode<A>, ListNode<B>> {

	public AbstractMapAction(BGraph g, ListNode<A> input, String cat) {
		super(g, input, cat);
	}

	@Override
	public ActionResult<ListNode<A>, ListNode<B>> exec(ChatNode chat) {
		var r = new ListNode<B>(g, "mapping to " + mapTo());
		inputNode.get().forEach(n -> r.get().add(map(n)));
		return createResultNode(r, true);
	}

	protected abstract B map(A n);

	@Override
	public String whatItDoes() {
		return "map nodes to their " + mapTo();
	}

	protected abstract String mapTo();
}