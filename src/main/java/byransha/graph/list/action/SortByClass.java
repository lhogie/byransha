package byransha.graph.list.action;

import byransha.graph.Element;

public class SortByClass extends Sort {

	public SortByClass(ListNode inputNode) {
		super(inputNode);
	}

	@Override
	public int compare(Element a, Element b) {
		return a.getClass().getName().compareTo(b.getClass().getName());
	}

	@Override
	protected String sortBy() {
		return "class";
	}

}
