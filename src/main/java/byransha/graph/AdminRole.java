package byransha.graph;

import byransha.system.Role;

public class AdminRole extends Role {

	public AdminRole(Hub g) {
		super(g);
	}

	@Override
	public boolean isAllowedToEdit(Element bNode) {
		return true;
	}

	@Override
	public boolean isAllowedToSee(Element n) {
		return true;
	}

}
