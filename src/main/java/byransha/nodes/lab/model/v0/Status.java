package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Status extends BusinessNode {

	StringNode name;

	public Status(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator);
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
