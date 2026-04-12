package byransha.graph.list.action;

import java.util.Comparator;

import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.Category.list;
import byransha.graph.ProcedureAction;

public abstract class Sort extends ProcedureAction<ListNode> implements Comparator<BNode> {

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
