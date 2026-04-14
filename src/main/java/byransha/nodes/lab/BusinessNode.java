package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.DocumentNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public abstract class BusinessNode extends BNode {
	@ShowInKishanView
	ListNode<DocumentNode> documents;

	public BusinessNode(BGraph g) {
		super(g);
	}

	@ShowInKishanView
	public ListNode<Issue> issues() {
		return exec("issues", Issue.class, i -> i.relatedTo);
	}

}
