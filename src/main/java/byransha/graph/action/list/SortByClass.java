package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class SortByClass extends Sort {

	public SortByClass(BGraph g, ListNode inputNode) {
		super(g, inputNode);
	}

	@Override
	public int compare(BNode a, BNode b) {
		return a.getClass().getName().compareTo(b.getClass().getName());
	}

	@Override
	protected String sortBy() {
		return "class";
	}

}
