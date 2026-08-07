package byransha.graph;

import byransha.system.Role;

public class AdminRole extends Role {

	public AdminRole(Hub g) {
		super(g);
	}

	@Override
	public boolean isAllowedToEdit(BNode bNode) {
		return true;
	}

	@Override
	public boolean isAllowedToSee(BNode n) {
		return true;
	}

}
