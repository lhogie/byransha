package byransha.list.action.filter;

import byransha.Element;
import byransha.action.Category.list;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;

public class RemoveSelected<N extends Element> extends FunctionAction<ListNode<N>, ListNode<N>> {

	public RemoveSelected(ListNode<N> l) {
		super(l, list.class);
	}

	@Override
	public boolean applies() {
		return true;// inputNode.selection.size() > 0;
	}

	@Override
	public String whatItDoes() {
		return "remove selection";
	}

	@Override
	protected void impl() throws Throwable {
		inputNode.elements.removeAll(inputNode.selection);
		inputNode.selection.clear();
	}
}