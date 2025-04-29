package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;

public class ResearchGroup extends Structure {
	public ResearchGroup(BBGraph g) {
		super(g);
		keywords = g.addNode(ListNode.class); //new ListNode<>(g);
		// TODO Auto-generated constructor stub
	}

	public ResearchGroup(BBGraph g, int id) {
		super(g, id);
		// TODO Auto-generated constructor stub
	}

	ListNode<ACMClassifier> keywords ;
}
