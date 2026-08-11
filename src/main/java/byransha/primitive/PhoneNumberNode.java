package byransha.primitive;

import byransha.ID;
import byransha.graph.Element;

public class PhoneNumberNode extends StringNode {

	public PhoneNumberNode(Element parent, ID id) {
		super(parent, id, "", "\\+?[0-9]+");
	}

	@Override
	public String whatIsThis() {
		return "a phone  number";
	}
}
