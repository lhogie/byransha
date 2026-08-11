package byransha.primitive;

import byransha.ID;
import byransha.service.system.Hub;

public class IDNode extends StringNode {

	public IDNode(Hub g, ID id) {
		super(g, id, "", "[0-9a-zA-Z]+");
	}

	@Override
	public String whatIsThis() {
		return "an editor for an element ID";
	}
}
