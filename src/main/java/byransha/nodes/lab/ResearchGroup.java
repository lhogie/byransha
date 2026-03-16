package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;

public class ResearchGroup extends Structure {
	public ListNode<ACMClassifier> keywords;

	public ResearchGroup(BGraph g) {
		super(g);
		keywords = new ListNode<>(g, "research group(s)");
	}

}
