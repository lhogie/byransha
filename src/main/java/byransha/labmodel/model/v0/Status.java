package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;

public class Status extends BusinessNode {
	StringNode name;

	public Status(BBGraph g) {
		super(g);
		name = g.addNode(StringNode.class); //new StringNode(g, null);
	}

	public Status(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "Status node";
	}

	@Override
	public String prettyName() {
		return name.get();
	}
}
