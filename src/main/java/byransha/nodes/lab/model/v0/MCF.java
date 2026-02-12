package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class MCF extends Status {
	public MCF(BBGraph g, User creator) {
		super(g, creator);
		name.set("Maitre de Conference", creator);
	}
}
