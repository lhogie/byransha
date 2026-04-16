package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;

public class Status extends BusinessNode {
	@ShowInKishanView
	StringNode name;

	public Status(BGraph g) {
		super(g);
		name = new StringNode(g);
	}

	@Override
	public String whatIsThis() {
		return "a position status defined by the employeer";
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
