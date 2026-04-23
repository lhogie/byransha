package byransha.graph;

import byransha.nodes.system.Role;

public class AdminRole extends Role {

	public AdminRole(BGraph g) {
		super(g);
	}

	@Override
	public boolean isAllowedToEdit(BNode bNode) {
		return true;
	}

}
