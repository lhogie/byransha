package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.DocumentNode;
import byransha.graph.ShowInKishanView;

public class Country extends BusinessNode {

	public String name, code;
	@ShowInKishanView
	public DocumentNode flag;

	public Country(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a country";
	}

	@Override
	public String toString() {
		return name + "(" + code + ")";
	}
}
