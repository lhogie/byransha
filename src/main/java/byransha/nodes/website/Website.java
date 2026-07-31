package byransha.nodes.website;

import byransha.graph.Root;
import byransha.graph.BNode;

public class Website extends BNode {

	protected Website(Root g) {
		super(g);
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
