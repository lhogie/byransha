package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;

public class Building extends BNode {

	public Building(BBGraph g) {
		super(g);
	}

	public Building(BBGraph g, int id){
		super(g, id);
	}

	@Override
	public String prettyName() {
		return "building";
	}

	
	@Override
	public String whatIsThis() {
		return "Building description";
	}
}
