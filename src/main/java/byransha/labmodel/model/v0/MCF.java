package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class MCF extends Status {
	public MCF(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		name.set("Maitre de Conference", creator);
		endOfConstructor();
	}
}
