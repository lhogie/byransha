package byransha.graph.list.action;

import byransha.graph.ProcedureAction;
import byransha.graph.Category.list;
import byransha.graph.Category.selection;

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