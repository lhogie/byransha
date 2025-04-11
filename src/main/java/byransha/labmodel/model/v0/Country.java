package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;
import byransha.ImageNode;
import byransha.StringNode;

public class Country extends BNode {
	StringNode name = new StringNode(graph, null);
	ImageNode flag;

	public Country(BBGraph g) {
		super(g);
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
