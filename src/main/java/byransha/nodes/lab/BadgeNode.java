package byransha.nodes.lab;

import byransha.nodes.primitive.StringNode;

public class BadgeNode extends StringNode {

	public BadgeNode(Person p) {
		super(p, null, ".+");
	}

}
