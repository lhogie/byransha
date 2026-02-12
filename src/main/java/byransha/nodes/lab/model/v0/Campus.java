package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Campus extends BusinessNode {

	public StringNode name;

	public ListNode<Building> buildings;

	public Campus(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator, "", ".+");
		buildings = new ListNode(g, creator);
	}

	@Override
	public String whatIsThis() {
		return "a campus";
	}

	@Override
	public String prettyName() {
		return name.get();
	}
}
