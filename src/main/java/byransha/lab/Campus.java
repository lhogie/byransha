package byransha.lab;

import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.StringNode;

public class Campus extends LabElement {
	@ShowInKishanView
	public StringNode name = fieldNode("name", id -> new StringNode(this, id, "", ".+"));

	@ShowInKishanView
	public ListNode<Building> buildings = fieldNode("buildings",
			id -> new ListNode(this, id, "building(s)", Building.class));

	public Campus(University parent, ID id) {
		super(parent, id);
	}

	@Override
	public String whatIsThis() {
		return "a campus";
	}

	@Override
	public String toString() {
		return name.get();
	}
}
