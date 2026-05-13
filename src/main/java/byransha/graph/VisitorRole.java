package byransha.graph;

import byransha.nodes.system.Role;

public class VisitorRole extends Role {

	public VisitorRole(BGraph g) {
		super(g);
	}

	@Override
	public boolean isAllowedToEdit(BNode bNode) {
		return false;
	}

	@Override
	public boolean isAllowedToSee(BNode n) {
		return true;
	}

}
