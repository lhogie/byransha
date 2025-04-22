package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.IntNode;
import byransha.ListNode;
import byransha.StringNode;

public class Office extends BusinessNode {
	StringNode name;
	ListNode<Person> users;
	IntNode surface;
	IntNode capacity;

	public Office(BBGraph g) {
		super(g);
	}

	public Office(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "an office";
	}

	@Override
	public String prettyName() {
		return "Office: " + (name != null ? name.get() : "Unnamed");
	}

	public double occupationRatio() {
		return ((double) capacity.get()) / users.size();
	}

	public double surfacePerUser() {
		return ((double) surface.get()) / users.size();
	}
}
