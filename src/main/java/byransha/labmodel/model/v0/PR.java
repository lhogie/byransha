package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class PR extends Status {

	public PR(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		name.set("Professeur des Universités", creator);
	}
}
