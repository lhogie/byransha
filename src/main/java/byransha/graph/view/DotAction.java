package byransha.graph.view;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public class DotAction extends NodeAction<ListNode, TextNode> {
	public DotAction(BGraph g, ListNode node) {
		super(g, node);
	}

	@Override
	public String whatItDoes() {
		return "generates a DOT showing out links";
	}

	@Override
	public ActionResult<ListNode, TextNode> exec() throws Throwable {
		var r = new TextNode(g, "DOT", inputNode.toDot());
		return createResultNode(r, true);
	}

	@Override
	public boolean applies() {
		return inputNode.size() > 0;
	}
}