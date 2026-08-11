package byransha.nodes.website;

import byransha.Element;
import byransha.service.system.Hub;

public class Website extends Element {

	protected Website(Hub g) {
		super(g, null);
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Deploy(this));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a web site";
	}

	@Override
	public String toString() {
		return "website";
	}

	public Page toHTMLPage() {
		return new Page();
	}

}
