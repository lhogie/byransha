package byransha.graph.list.action.filter;

import org.checkerframework.checker.units.qual.N;

import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;

public abstract class FilterNode<N extends BNode> extends FunctionAction<ListNode<N>, ListNode<N>> {

	public FilterNode(ListNode<N> inputNode, Class<? extends Category>... cat) {
		super(inputNode,  cat);
	}

	@Override
	public final String whatItDoes() {
		return "retain only" + retainsOnly();
	}

	public abstract String retainsOnly();

	@Override
	public void impl() {
		result = new ListNode<N>(g, retainsOnly());

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