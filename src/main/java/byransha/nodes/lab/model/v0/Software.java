package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.ByBoolean;
import byransha.nodes.system.User;

public class Software extends Publication {
	BooleanNode openSource;

	public Software(BBGraph g, User creator) {
		super(g, creator);
		openSource = new BooleanNode(g, creator, ByBoolean.DUNNO);
	}
}
