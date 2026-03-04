package byransha.nodes.system;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;

public class HistoryNode extends ListNode<BNode> {

	public HistoryNode(BGraph g) {
		super(g, "navigation history");
	}

	public void addToHistory(BNode n) {
		get().add(n);
	}
}
