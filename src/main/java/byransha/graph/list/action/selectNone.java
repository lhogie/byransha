package byransha.graph.list.action;

import byransha.graph.Category.list;
import byransha.graph.Category.selection;
import byransha.graph.ProcedureAction;

public class selectNone extends ProcedureAction<ListNode> {

	public selectNone(ListNode inputNode) {
		super(inputNode, list.class, selection.class);
	}

	@Override
	public String whatItDoes() {
		return "select none";
	}

	@Override
	protected void impl() throws Throwable {
		inputNode.selectNone();
	}

	@Override
	public boolean applies() {
		return inputNode.selection.size() > 0;
	}

}
