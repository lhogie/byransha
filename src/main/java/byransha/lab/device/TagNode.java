package byransha.lab.device;

import byransha.ID;
import byransha.graph.Element;
import byransha.primitive.StringNode;

public class TagNode extends StringNode {

	public TagNode(Element parent, ID id) {
		super(parent, id, null, ".+");
	}
}
