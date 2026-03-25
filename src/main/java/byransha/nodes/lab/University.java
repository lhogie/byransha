package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;

public class University extends Structure {
	ListNode<Campus> campuses;

	public University(BGraph g) {
		super(g);
		campuses = new ListNode(g, "campus");
	}
	
	@Override
	public String whatIsThis() {
		return "an university";
	}

}
