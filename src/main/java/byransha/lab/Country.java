package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.ImageElement;

public class Country extends LabElement {

	public String name, code;

	@ShowInKishanView
	public ImageElement flag = fieldNode("flag", id -> new ImageElement(this, id));

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
