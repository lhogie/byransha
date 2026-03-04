package byransha.graph;

import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class DummyNode extends BNode {
	StringNode aString;
	LongNode aNumber;

	protected DummyNode(BGraph g ) {
		super(g);
		aString = new StringNode(g,  "", null);
		aNumber = new LongNode(g);
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
