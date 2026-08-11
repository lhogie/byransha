package byransha.list.action.map;

import byransha.Element;
import byransha.action.Category;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;

public abstract class AbstractMapAction<A extends Element, B extends Element>
		extends FunctionAction<ListNode<A>, ListNode<B>> {

	public AbstractMapAction(ListNode<A> input, Class<? extends Category>... cat) {
		super(input, cat);
	}

	@Override
	public void impl() {
		result = new ListNode<B>(parent, null, "mapping to " + mapTo(), null);
		inputNode.get().forEach(n -> result.get().add(map(n)));
	}

	protected abstract B map(A n);

	@Override
	public String whatItDoes() {
		return "map nodes to their " + mapTo();
	}

	protected abstract String mapTo();
}