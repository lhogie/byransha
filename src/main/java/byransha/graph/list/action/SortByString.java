package byransha.graph.list.action;

import byransha.graph.Element;

public class SortByString extends Sort {

	public SortByString(ListNode inputNode) {
		super(inputNode);
	}

	@Override
	public int compare(Element a, Element b) {
		return a.toString().compareTo(b.toString());
	}

	@Override
	protected String sortBy() {
		return "text value";
	}

}
