package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public class University extends Structure {
	@ShowInKishanView
	ListNode<Campus> campuses = new ListNode(this, "campus", Campus.class);

	public University(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "an university";
	}

}
