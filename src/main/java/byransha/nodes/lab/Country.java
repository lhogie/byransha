package byransha.nodes.lab;

import byransha.graph.BBGraph;
import byransha.graph.DocumentNode;

public class Country extends BusinessNode {

	public String name, code;
	public DocumentNode flag;

	public Country(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a country";
	}

	@Override
	public String prettyName() {
		return name + "(" + code + ")";
	}
}
