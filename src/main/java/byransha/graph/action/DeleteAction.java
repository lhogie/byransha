package byransha.graph.action;

import byransha.graph.BBGraph;

public class DeleteAction extends ConfirmRequiredNodeAction {

	public DeleteAction(BBGraph g) {
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