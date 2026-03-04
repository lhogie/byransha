package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Status extends BusinessNode {

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
	public String prettyName() {
		return name.get();
	}
}
