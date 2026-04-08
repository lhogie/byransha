package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.system.ChatNode;

public class DotAction extends NodeAction<ListNode, TextNode> {
	public DotAction(BGraph g, ListNode node) {
		super(g, node, "list/export");
	}

	@Override
	public String whatItDoes() {
		return "generates a DOT showing out links";
	}

	@Override
	public ActionResult<ListNode, TextNode> exec(ChatNode chat) throws Throwable {
		var r = new TextNode(g, "DOT", inputNode.toDot());
		return createResultNode(r, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return inputNode.elements.size() > 0;
	}
}