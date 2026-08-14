package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.StringNode;

public class Status extends LabElement {
	@ShowInKishanView
	StringNode name= fieldNode("name",  id ->new StringNode(this, id, null, ".+"));

	public Status(InstantiationParameters p) {
		super(p);
	}

	public Status(Element parent, ID id) {
		super(parent,id);
	}

	@Override
	public String whatIsThis() {
		return "a position status defined by the employeer";
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
