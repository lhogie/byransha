package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class IR extends Status {
	public IR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Ingénieur de Recherche", creator);
	}
}
