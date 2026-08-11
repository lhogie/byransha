package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.BooleanNode;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;

public class Room extends LabNode {
	@ShowInKishanView
	public StringNode name = fieldNode("name", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final ListNode<Person> users = fieldNode("users", id -> new ListNode(this, id, "users", Person.class));

	@ShowInKishanView
	public LongNode surface = fieldNode("surface", id -> new LongNode(this, id));

	@ShowInKishanView
	public LongNode capacity = fieldNode("capacity", id -> new LongNode(this, id));

	@ShowInKishanView
	public BooleanNode isZZR;

	@ShowInKishanView
	public LongNode floorNumber;

	public Room(Element parent, ID id) {
		super(parent, id);
	}

	@Override
	public String whatIsThis() {
		return "an office";
	}

	@Override
	public String toString() {
		if (name != null) {
			return "Office: " + name.get();
		}
		return null;
	}

	public double occupationRatio() {
		return ((double) capacity.get()) / users.elements.size();
	}

	public double surfacePerUser() {
		return ((double) surface.get()) / users.elements.size();
	}
}
