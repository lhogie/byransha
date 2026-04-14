package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.TextNode;

public class Issue extends BusinessNode {
	@ShowInKishanView
	ListNode<BusinessNode> relatedTo = new ListNode<>(g, "related to", BusinessNode.class);

	@ShowInKishanView
	TextNode description = new TextNode(g, "", ".+");

	public Issue(BGraph g) {
		super(g);
	}

}
