package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class BadgeNode extends StringNode {

	public BadgeNode(BNode p) {
		super(p, null, ".+");
	}

}
