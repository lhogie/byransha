package byransha.nodes.primitive;

import byransha.graph.BGraph;

public class IDNode extends StringNode {

	public IDNode(BGraph g) {
		super(g, null, "(-)?[0-9a-zA-Z]+");
	}

	@Override
	public String whatIsThis() {
		return "an editor for a node ID";
	}
}
