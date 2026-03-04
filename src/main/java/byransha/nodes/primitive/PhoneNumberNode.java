package byransha.nodes.primitive;

import byransha.graph.BGraph;

public class PhoneNumberNode extends StringNode {

	public PhoneNumberNode(BGraph db) {
		super(db);
	}

	@Override
	public String prettyName() {
		if (get() == null || get().isEmpty()) {
			return "Phone number (empty)";
		}

		return getAsString();
	}

	@Override
	public String whatIsThis() {
		return "PhoneNumberNode with value: " + get();
	}
}
