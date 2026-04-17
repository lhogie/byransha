package byransha.nodes.lab;

import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;

public class Office extends BusinessNode {
	@ShowInKishanView
	public StringNode name = new StringNode(parent);;

	@ShowInKishanView
	public final ListNode<Person> users = new ListNode(parent, "users", Person.class);

	@ShowInKishanView
	public LongNode surface = new LongNode(parent), capacity = new LongNode(parent);

	@ShowInKishanView
	public BooleanNode isZZR;

	@ShowInKishanView
	public LongNode floorNumber;

	public Office(Building g) {
		super(g);
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
