package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;

public class Status extends BusinessNode {
	StringNode name;

	public Status(BBGraph g, User creator) {
		super(g, creator);
		name = new StringNode(g, creator); //new StringNode(g, null);
		endOfConstructor();
	}

	public Status(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

	@Override
	public String whatIsThis() {
		return "Status node";
	}

	@Override
	public String prettyName() {
		if(name == null || name.get() == null || name.get().isEmpty()) {
			return "Unnamed Status";
		}
		return name.get();
	}
}
