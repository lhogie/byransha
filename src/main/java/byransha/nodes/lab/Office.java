package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;

public class Office extends BusinessNode {

	public StringNode name;

	public ListNode<Person> users;

	public LongNode surface, capacity;

	public Office(BGraph g) {
		super(g);
		name = new StringNode(g);
		users = new ListNode(g, "users");
		surface = new LongNode(g);
		capacity = new LongNode(g);
	}

	@Override
	public String whatIsThis() {
		return "an office";
	}

	@Override
	public String prettyName() {
		if (name != null) {
			return "Office: " + name.get();
		}
		return null;
	}

	public double occupationRatio() {
		return ((double) capacity.get()) / users.size();
	}

	public double surfacePerUser() {
		return ((double) surface.get()) / users.size();
	}
}
