package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class MCF extends Status {
	public MCF(BBGraph g) {
		super(g);
		name.set("Maitre de Conference");
	}

	public MCF(BBGraph g, int id) {
		super(g, id);
	}
}
