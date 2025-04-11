package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.StringNode;

public class Status extends BNode {
	StringNode name = new StringNode(graph, null);

	public Status(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "Status node";
	}

	@Override
	protected String prettyName() {
		return name.get();
	}
}
