package byransha.labmodel.model.v0;

import byransha.BBGraph;

public class EPST extends Structure {

	public EPST(BBGraph g) {
		super(g);
		status.add(new IR(g));
		status.add(new CR(g));
		status.add(new DR(g));
	}

	public EPST(BBGraph g, int id) {
		super(g, id);
	}
}
