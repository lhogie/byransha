package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class EPST extends Structure {

	public EPST(BBGraph g, User creator) {
		super(g,  creator);
		status.add(new IR(g, creator), creator);
		status.add(new CR(g, creator), creator);
		status.add(new DR(g, creator), creator);
		endOfConstructor();
	}

	public EPST(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}
}
