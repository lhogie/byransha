package byransha.graph.list.action.filter;

import byransha.graph.BNode;
import byransha.graph.Category.list;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;

public abstract class ListFilter<N extends BNode> extends FunctionAction<ListNode<N>, ListNode<N>> {

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
		result = new ListNode<N>(parent, retainsOnly(), inputNode.contentClass);

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