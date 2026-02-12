package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Office extends BusinessNode {

	public StringNode name;

	public ListNode<Person> users;

	public IntNode surface, capacity;

	public Office(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator);
		users = new ListNode(g, creator);
		surface = new IntNode(g, creator);
		capacity = new IntNode(g, creator);
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
