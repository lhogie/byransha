package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class DR extends Status {
	public DR(BBGraph g, User creator) {
		super(g,  creator);
		name.set("Directeur de Recherche", creator);
		endOfConstructor();
	}

	public DR(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}
}
