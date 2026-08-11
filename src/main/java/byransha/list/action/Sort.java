package byransha.list.action;

import java.util.Comparator;

import byransha.Element;
import byransha.action.Category;
import byransha.action.ProcedureAction;
import byransha.action.Category.list;

public abstract class Sort extends ProcedureAction<ListNode> implements Comparator<Element> {

	public static class sort extends Category {
	}

	public Sort(ListNode inputNode) {
		super(inputNode, list.class, sort.class);
	}

	@Override
	public String whatItDoes() {
		return "sort by " + sortBy();
	}

	protected abstract String sortBy();

	@Override
	public void impl() throws Throwable {
		inputNode.elements.sort(this);
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

}
