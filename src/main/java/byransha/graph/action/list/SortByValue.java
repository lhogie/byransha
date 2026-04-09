package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;

public class SortByValue extends Sort {

	public SortByValue(BGraph g, ListNode inputNode) {
		super(g, inputNode);
	}

	@Override
	public int compare(BNode a, BNode b) {
		if (a instanceof ValuedNode va && a instanceof ValuedNode vb) {
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
