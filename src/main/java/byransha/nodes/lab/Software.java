package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.BooleanNode;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BGraph g) {
		super(g);
		openSource = new BooleanNode(g, null);
	}
}
