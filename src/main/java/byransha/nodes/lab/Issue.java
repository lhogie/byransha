package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.TextNode;

public class Issue extends BusinessNode {
	@ShowInKishanView
	public final ListNode<BusinessNode> relatedTo = new ListNode<>(this, "related to", BusinessNode.class);

	@ShowInKishanView
	TextNode description = new TextNode(this, "", ".+");

	public Issue(BGraph g) {
		super(g);
	}
}
