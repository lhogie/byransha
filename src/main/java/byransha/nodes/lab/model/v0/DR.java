package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class DR extends Status {
	public DR(BBGraph g, User creator) {
		super(g,  creator);
		name.set("Directeur de Recherche", creator);
	}
}
