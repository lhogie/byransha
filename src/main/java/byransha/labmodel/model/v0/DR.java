package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class DR extends Status {
	public DR(BBGraph g) {
		super(g);
		name.set("Directeur de Recherche");
	}

	public DR(BBGraph g, int id) {
		super(g, id);
	}
}
