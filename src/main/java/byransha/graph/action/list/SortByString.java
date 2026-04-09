package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class SortByString extends Sort {

	public SortByString(BGraph g, ListNode inputNode) {
		super(g, inputNode);
	}

	@Override
	public int compare(BNode a, BNode b) {
		return a.toString().compareTo(b.toString());
	}
	@Override
	protected String sortBy() {
		return "text value";
	}

}
