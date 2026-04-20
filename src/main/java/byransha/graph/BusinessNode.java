package byransha.graph;

import byransha.NewNodeEvent;
import byransha.graph.list.action.ListNode;
import byransha.nodes.lab.Issue;

public abstract class BusinessNode extends BNode {
	@ShowInKishanView
	public final ListNode<DocumentNode> documents = new ListNode<>(this, "document(s)", DocumentNode.class);

	public BusinessNode(BNode parent) {
		super(parent);
	}

	@ShowInKishanView
	public ListNode<Issue> issues() {
		return inverseRelation("issues", Issue.class, i -> i.relatedTo);
	}
}
