package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Campus extends BusinessNode {
	@ShowInKishanView
	public StringNode name = new StringNode(this, "", ".+");

	@ShowInKishanView
	public ListNode<Building> buildings = new ListNode(this, "building(s)", Building.class);

	public Campus(BNode parent) {
		super(parent);
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
