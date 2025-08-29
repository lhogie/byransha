package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;
import byransha.annotations.Required;

public class Status extends BusinessNode {

	@Required
	StringNode name;

	public Status(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		name = new StringNode(g, creator, InstantiationInfo.persisting);
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
