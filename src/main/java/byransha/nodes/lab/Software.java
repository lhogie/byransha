package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BNode g) {
		super(g);
		openSource = new BooleanNode(g, null);
	}
}
