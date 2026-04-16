package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Campus extends BusinessNode {
	@ShowInKishanView
	public StringNode name = new StringNode(g, "", ".+");

	@ShowInKishanView
	public ListNode<Building> buildings = new ListNode(g, "building(s)", Building.class);

	public Campus(BGraph g) {
		super(g);
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
