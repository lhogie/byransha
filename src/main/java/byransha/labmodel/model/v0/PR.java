package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class PR extends Status {

	public PR(BBGraph g) {
		super(g);
		name.set("Professeur des Universités");
	}

	public PR(BBGraph g, int id) {
		super(g, id);
	}

}
