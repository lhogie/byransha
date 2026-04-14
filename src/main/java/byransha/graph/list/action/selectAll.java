package byransha.graph.list.action;

import byransha.graph.ProcedureAction;
import byransha.graph.Category.list;
import byransha.graph.Category.selection;

final class selectAll extends ProcedureAction<ListNode> {
	selectAll(ListNode inputNode) {
		super(inputNode, list.class, selection.class);
	}

	@Override
	public String whatItDoes() {
		return "select all";
	}

	@Override
	public void impl() {
		inputNode.selectAll();
	}

	@Override
	public boolean applies() {
		return inputNode.selection.size() < inputNode.elements.size();
	}
}