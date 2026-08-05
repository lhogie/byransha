package byransha.primitive;

import byransha.graph.Hub;

public class IDNode extends StringNode {

	public IDNode(Hub g) {
		super(g, null, "(-)?[0-9a-zA-Z]+");
	}

	@Override
	public String whatIsThis() {
		return "an editor for a node ID";
	}
}
