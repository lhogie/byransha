package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class CR extends Status {
	public CR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Chargé de Recherche", creator);
	}

	public CR(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}
}
