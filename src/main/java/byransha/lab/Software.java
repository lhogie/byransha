package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.primitive.BooleanNode;

public class Software extends Publication {
	final BooleanNode openSource= fieldNode("surface", id -> new BooleanNode(this, id, null));

	public Software(Element g, ID id) {
		super(g, id);
	}
}
