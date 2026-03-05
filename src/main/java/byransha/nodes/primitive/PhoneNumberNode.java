package byransha.nodes.primitive;

import byransha.graph.BGraph;

public class PhoneNumberNode extends StringNode {

	public PhoneNumberNode(BGraph db) {
		super(db);
	}

	@Override
	public String whatIsThis() {
		return "a phone  number";
	}
}
