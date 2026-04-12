package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Campus extends BusinessNode {

	public StringNode name;

	public ListNode<Building> buildings;

	public Campus(BGraph g) {
		super(g);
		name = new StringNode(g, "", ".+");
		buildings = new ListNode(g, "campus(es)");
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
