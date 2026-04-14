package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;

public class Office extends BusinessNode {
	@ShowInKishanView
	public StringNode name = new StringNode(g);;

	@ShowInKishanView
	public final ListNode<Person> users = new ListNode(g, "users", Person.class);

	@ShowInKishanView
	public LongNode surface = new LongNode(g), capacity = new LongNode(g);

	@ShowInKishanView
	public BooleanNode isZZR;

	@ShowInKishanView
	public LongNode floorNumber;

	public Office(BGraph g) {
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
