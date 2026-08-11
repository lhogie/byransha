package byransha.access_control;

import byransha.Element;
import byransha.service.system.Hub;

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
