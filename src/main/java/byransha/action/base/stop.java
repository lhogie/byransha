package byransha.action.base;

import byransha.action.Action;
import byransha.action.ProcedureAction;

public class stop extends ProcedureAction<Action> {

	public stop(Action inputNode) {
		super(inputNode, action.class);
	}

	@Override
	public String whatItDoes() {
		return "stop";
	}

	@Override
	public void impl() {
		inputNode.stopRequested = true;
	}

	@Override
	public boolean applies() {
		return inputNode.hasAlreadyBeenStarted() && !inputNode.stopRequested;
	}

}
