package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Building extends BusinessNode {

	@ShowInKishanView
	public ListNode<Room> offices = new ListNode(this, "office(s)", Room.class);
	@ShowInKishanView
	public StringNode name;

	public Building(BNode parent) {
		super(parent);
		name = new StringNode(parent, "", ".+");
	}

	public Room findOffice(String name) {
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
