package byransha.graph.view;

import byransha.graph.BGraph;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public class DotAction extends NodeAction<ListNode<ClassNode>, byransha.nodes.primitive.TextNode> {
	public DotAction(BGraph g, ListNode<ClassNode> node) {
		super(g, node);
	}

	@Override
	public String whatItDoes() {
		return "generate a DOT";
	}

	@Override
	public ActionResult<ListNode<ClassNode>, TextNode> exec() throws Throwable {
		var r = new TextNode(g, "DOT", inputNode.toDot());
		return createResultNode(r, true);
	}
}