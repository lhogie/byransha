package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public abstract class BusinessNode extends BNode {

	public BusinessNode(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	public BusinessNode(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

}
