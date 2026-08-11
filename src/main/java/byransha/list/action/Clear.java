package byransha.list.action;

import byransha.action.ProcedureAction;
import byransha.action.Category.list;

public class Clear extends ProcedureAction<ListNode> {

	public Clear(ListNode inputNode) {
		super(inputNode, list.class);
	}

	@Override
	public String whatItDoes() {
		return "clear";
	}

	@Override
	public void impl() throws Throwable {
		inputNode.elements.clear();
	}

	@Override
	public boolean applies() {
		return true;
	}

}
