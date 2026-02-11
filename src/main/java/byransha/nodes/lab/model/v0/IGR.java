package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.system.User;

public class IGR extends Status {
	public IGR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Ingénieur de Recherche Université", creator);
	}
}
