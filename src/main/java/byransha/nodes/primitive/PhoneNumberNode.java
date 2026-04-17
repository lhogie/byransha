package byransha.nodes.primitive;

import byransha.graph.BNode;

public class PhoneNumberNode extends StringNode {

	public PhoneNumberNode(BNode db) {
		super(db);
	}

	@Override
	public String whatIsThis() {
		return "a phone  number";
	}
}
