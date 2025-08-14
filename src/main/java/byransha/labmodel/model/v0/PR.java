package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class PR extends Status {

	public PR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Professeur des Universités", creator);
		endOfConstructor();
	}

	public PR(BBGraph g, int id, User creator) {
		super(g, creator, id);
		endOfConstructor();
	}

}
