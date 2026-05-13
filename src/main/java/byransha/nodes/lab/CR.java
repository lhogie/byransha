package byransha.nodes.lab;

import byransha.graph.BNode;

public class CR extends Status {
	public CR(BNode g) {
		super(g);
		name.set("Chargé de Recherche");
		name.readOnly = true;
	}
}
