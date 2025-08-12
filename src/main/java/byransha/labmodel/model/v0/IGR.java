package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class IGR extends Status {
	public IGR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Ingénieur de Recherche Université", creator);
	}

	public IGR(BBGraph g, int id, User creator) {
		super(g, creator, id);
	}
}
