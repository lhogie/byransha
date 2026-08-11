package byransha.graph;

import byransha.system.Role;

public class VisitorRole extends Role {

	public VisitorRole(Hub g) {
		super(g);
	}

	@Override
	public boolean isAllowedToEdit(Element bNode) {
		return false;
	}

	@Override
	public boolean isAllowedToSee(Element n) {
		return true;
	}

}
