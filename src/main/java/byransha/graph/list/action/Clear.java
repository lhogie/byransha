package byransha.graph.list.action;

import byransha.graph.Category.list;
import byransha.graph.ProcedureAction;

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
