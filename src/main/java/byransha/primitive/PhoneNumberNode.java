package byransha.primitive;

import byransha.Element;
import byransha.ID;

public class PhoneNumberNode extends StringNode {

	public PhoneNumberNode(Element parent, ID id) {
		super(parent, id, "", "\\+?[0-9]+");
	}

	@Override
	public String whatIsThis() {
		return "a phone  number";
	}
}
