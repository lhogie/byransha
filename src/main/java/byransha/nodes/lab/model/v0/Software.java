package byransha.nodes.lab.model.v0;

import byransha.nodes.primitive.BooleanNode;
import byransha.BBGraph;
import byransha.nodes.system.User;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BBGraph g, User creator) {
		super(g, creator);
		openSource = new BooleanNode(g, creator, true);
	}
}
