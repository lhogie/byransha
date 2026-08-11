package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;

public class Country extends LabNode {

	public String name, code;

	@ShowInKishanView
	public ImageNode flag = fieldNode("flag", id -> new ImageNode(this, id));

	public Country(Element g, ID id) {
		super(g, id);
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
