package byransha.list.action;

import byransha.Element;
import byransha.primitive.ValuedElement;

public class SortByValue extends Sort {

	public SortByValue(ListNode inputNode) {
		super(inputNode);
	}

	@Override
	public int compare(Element a, Element b) {
		if (a instanceof ValuedElement va && a instanceof ValuedElement vb) {
			var valueOfA = va.get();
			var valueOfB = vb.get();

			if (valueOfA instanceof Comparable ca && valueOfA instanceof Comparable cb) {
				return ca.compareTo(cb);
			} else {
				return valueOfA.toString().compareTo(valueOfB.toString());
			}
		} else {
			return a.toString().compareTo(b.toString());
		}
	}

	@Override
	protected String sortBy() {
		return "value (when possible)";
	}

}
