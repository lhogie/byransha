package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class IGR extends Status {
	public IGR(BBGraph g) {
		super(g);
		name.set("Ingénieur de Recherche Université");
	}

	public IGR(BBGraph g, int id) {
		super(g, id);
	}
}
