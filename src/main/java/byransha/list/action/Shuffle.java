package byransha.list.action;

import byransha.action.ProcedureAction;
import byransha.action.Category.list;

public class Shuffle extends ProcedureAction<ListNode> {

	public Shuffle(ListNode inputNode) {
		super(inputNode, list.class);
	}

	@Override
	public String whatItDoes() {
		return "shuffle";
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

	@Override
	public void impl() {
		inputNode.shuffle();
	}

}
