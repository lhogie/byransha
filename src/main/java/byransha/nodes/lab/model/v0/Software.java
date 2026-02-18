package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.system.User;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BBGraph g) {
		super(g);
		openSource = new BooleanNode(g, null);
	}
}
