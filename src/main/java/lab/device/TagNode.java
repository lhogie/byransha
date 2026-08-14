package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.primitive.StringNode;

public class TagNode extends StringNode {

	public TagNode(Element parent, ID id) {
		super(parent, id, null, ".+");
	}
}
