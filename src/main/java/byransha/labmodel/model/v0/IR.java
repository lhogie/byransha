package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.User;

public class IR extends Status {
	public IR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Ingénieur de Recherche", creator);
	}

	public IR(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}
}
