package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class DR extends Status {
	public DR(BBGraph g) {
		super(g);
		name.set("Directeur de Recherche");
	}
}
