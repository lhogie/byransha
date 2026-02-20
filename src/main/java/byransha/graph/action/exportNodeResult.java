package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.TextNode;

public final class exportNodeResult extends ActionResult<BNode, ListNode<TextNode>> {
	public ListNode<TextNode> texts;

	protected exportNodeResult(BBGraph g, NodeAction<BNode, ListNode<TextNode>> runningAction,
			ListNode<TextNode> result) {
		super(g, runningAction, result);
		this.texts = new ListNode<>(g);
	}

}