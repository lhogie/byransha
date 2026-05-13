package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;

public class Status extends BusinessNode {
	@ShowInKishanView
	StringNode name;

	public Status(BNode g) {
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
