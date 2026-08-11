package byransha.list.action;

import java.util.HashSet;

import byransha.action.ProcedureAction;
import byransha.action.Category.list;

public class Uniq extends ProcedureAction<ListNode> {

	public Uniq(ListNode inputNode) {
		super(inputNode, list.class);
	}

	@Override
	public String whatItDoes() {
		return "remove duplicates";
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

	@Override
	public void impl() throws Throwable {
		var s = new HashSet<>(inputNode.elements);
		inputNode.elements.clear();
		inputNode.elements.addAll(s);
	}

}
