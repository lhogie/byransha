package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;

public class University extends Structure {

	ListNode<Campus> campuses;

	public University(BBGraph g) {
		super(g);
		campuses = new ListNode<>(g);
		status.add(new IGR(g));
		status.add(new MCF(g));
		status.add(new PR(g));
	}

	public University(BBGraph g, int id) {
		super(g, id);
	}
}
