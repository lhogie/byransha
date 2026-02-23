package byransha.nodes.system;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;

public class HistoryNode extends ListNode<BNode> {

	public HistoryNode(BBGraph g) {
		super(g);
	}

	public void addToHistory(BNode n) {
		get().add(n);
	}
}
