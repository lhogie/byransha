package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Building extends BusinessNode {

	public ListNode<Office> offices;
	public StringNode name;

	public Building(BGraph g) {
		super(g);
		offices = new ListNode(g, "office(s)");
		name = new StringNode(g, "", ".+");
	}

	public Office findOffice(String name) {
		for (var o : offices.elements()) {
			if (o.name.get().equals(name)) {
				return o;
			}
		}

		return null;
	}

	@Override
	public String prettyName() {
		return name.get();
	}

	@Override
	public String whatIsThis() {
		return "a building in a campus";
	}
}
