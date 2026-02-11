package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;
import byransha.annotations.Required;

public class Status extends BusinessNode {

	@Required
	StringNode name;

	public Status(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator);
	}


	@Override
	public String whatIsThis() {
		return "Status node";
	}

	@Override
	public String prettyName() {
		if(name != null && name.get() != null) return name.get();
		return null;
	}
}
