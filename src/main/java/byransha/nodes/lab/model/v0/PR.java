package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.system.User;

public class PR extends Status {

	public PR(BBGraph g, User creator) {
		super(g, creator);
		name.set("Professeur des Universités", creator);
	}
}
