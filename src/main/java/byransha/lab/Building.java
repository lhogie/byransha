package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;

public class Building extends LabNode {

	@ShowInKishanView
	public ListNode<Room> offices = fieldNode("offices", id -> new ListNode(this, id, "office(s)", Room.class));
	@ShowInKishanView
	public StringNode name = fieldNode("name", id -> new StringNode(parent, id, "", ".+"));

	public Building(Element parent, ID id) {
		super(parent, id);
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
