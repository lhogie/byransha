package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public final class exportNodeResult extends ActionResult<BNode, ListNode<TextNode>> {

	protected exportNodeResult(BGraph g, NodeAction<BNode, ListNode<TextNode>> runningAction,
			ListNode<TextNode> result) {
		super(g, runningAction, result, true);
	}

}