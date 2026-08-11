package byransha.list.action;

import byransha.action.ProcedureAction;
import byransha.action.Category.list;
import byransha.action.Category.selection;

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
