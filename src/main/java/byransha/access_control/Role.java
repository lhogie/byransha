package byransha.access_control;

import byransha.Element;

public abstract class Role extends Element {

	public Role(Element parent) {
		super(parent, null);
	}

	public abstract boolean isAllowedToEdit(Element n);

	public abstract boolean isAllowedToSee(Element n);

}
