package byransha.graph;

import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class DummyNode extends BNode {
	StringNode aString;
	IntNode aNumber;

	protected DummyNode(BBGraph g ) {
		super(g);
		aString = new StringNode(g,  "", null);
		aNumber = new IntNode(g);
		aNumber.setBounds(0, 10);
	}

	@Override
	public String whatIsThis() {
		return "a stupid node";
	}

	@Override
	public String prettyName() {
		return "stupid node";
	}

}
