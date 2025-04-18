package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;
import byransha.ImageNode;
import byransha.StringNode;

public class Country extends BNode {
	StringNode name ;
	ImageNode flag;

	public Country(BBGraph g) {
		super(g);
		name = new StringNode(g, null);
	}

	public Country(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "Country: " + (name != null ? name.toString() : "Unnamed");
	}
	

	@Override
	public String prettyName() {
		return name.get();
	}
}
