package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Campus extends BusinessNode {

	public StringNode name;

	public ListNode<Building> buildings;

	public Campus(BBGraph g) {
		super(g);
		name = new StringNode(g, "", ".+");
		buildings = new ListNode(g);
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
