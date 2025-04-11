package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;

public class Campus extends BNode {
	StringNode name = new StringNode(graph, null);
	ListNode<Office> offices = new ListNode<>(graph);

	public Campus(BBGraph g) {
		super(g);
	}

	@Override
	public String prettyName() {
		return "campus";
	}

	
	@Override
	public String whatIsThis() {
		return "Campus: " + name.get();
	}

//	ListNode<Building> buildings;
	public Office findOffice(String name) {
		for (var o : offices.l) {
			if (o.name.get().equals(name)) {
				return o;
			}
		}

		return null;
	}
}
