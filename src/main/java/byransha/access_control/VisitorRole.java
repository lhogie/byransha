package byransha.access_control;

import byransha.Element;
import byransha.service.system.Hub;

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
