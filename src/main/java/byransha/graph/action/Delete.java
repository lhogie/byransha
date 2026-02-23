package byransha.graph.action;

import byransha.graph.BBGraph;

public class Delete extends ConfirmRequiredNodeAction {

	public Delete(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItDoes() {
		return "delete from the graph";
	}

	@Override
	protected ActionResult execConfirm() {
		delete();
		return null;
	}
}