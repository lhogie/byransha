package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;

public class Building extends BNode {

	public Building(BBGraph g) {
		super(g);
	}

	@Override
	protected String prettyName() {
		return "building";
	}

	
	@Override
	public String whatIsThis() {
		return "Building description";
	}
}
