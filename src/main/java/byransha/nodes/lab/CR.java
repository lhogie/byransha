package byransha.nodes.lab;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class CR extends Status {
	public CR(BBGraph g) {
		super(g);
		name.set("Chargé de Recherche");
		name.readOnly = true;
	}
}
