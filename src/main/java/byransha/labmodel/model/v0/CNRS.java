package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class CNRS extends EPST {

	public CNRS(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	public CNRS(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

}
