package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;

public class Country extends BusinessNode {

	public String name, code;
	@ShowInKishanView
	public byte[] flag;

	public Country(BNode g) {
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
