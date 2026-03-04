package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.system.User;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BGraph g) {
		super(g);
		openSource = new BooleanNode(g, null);
	}
}
