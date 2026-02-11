package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;

public abstract class BusinessNode extends BNode {

	public BusinessNode(BBGraph g, User creator) {
		super(g, creator);
	}
}
