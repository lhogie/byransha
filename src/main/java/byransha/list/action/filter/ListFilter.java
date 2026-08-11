package byransha.list.action.filter;

import byransha.Element;
import byransha.action.Category.list;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;

public abstract class ListFilter<N extends Element> extends FunctionAction<ListNode<N>, ListNode<N>> {

	public ListFilter(ListNode<N> inputNode) {
		super(inputNode, list.class);
	}

	@Override
	public final String whatItDoes() {
		return "retain only" + retainsOnly();
	}

	public abstract String retainsOnly();

	@Override
	public void impl() {
		result = new ListNode<N>(this, null, retainsOnly(), inputNode.contentClass);

		inputNode.get().forEach(n -> {
			if (retains(n)) {
				result.get().add(n);
			}
		});
	}

	public abstract boolean retains(N n);

	@Override
	public abstract boolean applies();

}