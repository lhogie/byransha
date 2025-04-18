package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class CR extends Status {
	public CR(BBGraph g) {
		super(g);
		name.set("Chargé de Recherche");
	}

	public CR(BBGraph g, int id) {
		super(g, id);
	}
}
