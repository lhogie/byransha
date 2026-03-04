package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.system.User;

public class CR extends Status {
	public CR(BGraph g) {
		super(g);
		name.set("Chargé de Recherche");
		name.readOnly = true;
	}
}
