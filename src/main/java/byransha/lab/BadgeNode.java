package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.primitive.StringNode;

public class BadgeNode extends StringNode {

	public BadgeNode(Element p, ID id) {
		super(p, id, null, ".+");
	}

}
