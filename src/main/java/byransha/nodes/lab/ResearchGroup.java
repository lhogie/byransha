package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;

public class ResearchGroup extends Structure {
	public ListNode<ACMClassifier> keywords;

	public ResearchGroup(BGraph g, String name) {
		super(g);
		this.name.set(name);
		keywords = new ListNode<>(g, "research group(s)");
	}

}
