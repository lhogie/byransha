package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Building extends BusinessNode {

	@ShowInKishanView
	public ListNode<Office> offices = new ListNode(g, "office(s)", Office.class);
	@ShowInKishanView
	public StringNode name;

	public Building(BGraph g) {
		super(g);
		name = new StringNode(g, "", ".+");
	}

	public Office findOffice(String name) {
		for (var o : offices.elements) {
			if (o.name.get().equals(name)) {
				return o;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return name.get();
	}

	@Override
	public String whatIsThis() {
		return "a building in a campus";
	}
}
