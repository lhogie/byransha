package byransha.labmodel.model.v0;

import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.User;

public class Software extends Publication {
	public Software(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		openSource = new BooleanNode(g, creator, InstantiationInfo.persisting); //new BooleanNode(g);
	}

	BooleanNode openSource;

}
