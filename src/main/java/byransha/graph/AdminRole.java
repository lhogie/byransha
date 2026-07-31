package byransha.graph;

import byransha.nodes.system.Role;

public class AdminRole extends Role {

	public AdminRole(Root g) {
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
