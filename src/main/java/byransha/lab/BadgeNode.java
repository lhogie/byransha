package byransha.lab;

import byransha.graph.BNode;
import byransha.primitive.StringNode;

public class BadgeNode extends StringNode {

	public BadgeNode(BNode p) {
		super(p, null, ".+");
	}

}
