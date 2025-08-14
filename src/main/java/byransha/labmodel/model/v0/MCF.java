package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class MCF extends Status {
	public MCF(BBGraph g, User creator) {
		super(g, creator);
		name.set("Maitre de Conference", creator);
		endOfConstructor();
	}

	public MCF(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}
}
