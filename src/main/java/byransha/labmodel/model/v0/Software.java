package byransha.labmodel.model.v0;

import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.User;

public class Software extends Publication {
	public Software(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		openSource = new BooleanNode(g, creator, InstantiationInfo.persisting); //new BooleanNode(g);
		endOfConstructor();
	}

	BooleanNode openSource;

}
