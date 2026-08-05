package byransha.lab;

import byransha.graph.BNode;
import byransha.primitive.StringNode;

public class Nationality extends StringNode {

	public Nationality(BNode g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "Nationality" + (get() != null ? " " + get() : "");
	}
}
