package lab;

import byransha.Element;
import byransha.ID;
import byransha.primitive.StringNode;

public class BadgeNode extends StringNode {

	public BadgeNode(Element p, ID id) {
		super(p, id, null, ".+");
	}

}
