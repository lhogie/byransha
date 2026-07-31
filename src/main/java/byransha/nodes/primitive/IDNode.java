package byransha.nodes.primitive;

import byransha.graph.Root;

public class IDNode extends StringNode {

	public IDNode(Root g) {
		super(g, null, "(-)?[0-9a-zA-Z]+");
	}

	@Override
	public String whatIsThis() {
		return "an editor for a node ID";
	}
}
