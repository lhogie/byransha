package byransha.list.action;

import byransha.action.ProcedureAction;
import byransha.action.Category.list;
import byransha.action.Category.selection;

final class invertSelection extends ProcedureAction<ListNode> {
	invertSelection(ListNode inputNode) {
		super(inputNode, list.class, selection.class);
	}

	@Override
	public String whatItDoes() {
		return "invert selection";
	}

	@Override
	public void impl() {
		inputNode.invertSelection();
	}

	@Override
	public boolean applies() {
		return true;
	}
}