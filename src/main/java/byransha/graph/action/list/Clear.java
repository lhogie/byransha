package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode.list;
import byransha.nodes.system.ChatNode;

public class Clear extends NodeAction<ListNode, ListNode> {

	public Clear(BGraph g, ListNode inputNode) {
		super(g, inputNode, list.class);
	}

	@Override
	public String whatItDoes() {
		return "clear";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		inputNode.elements.clear();
		return createResultNode(inputNode, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
