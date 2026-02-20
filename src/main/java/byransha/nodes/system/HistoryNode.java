package byransha.nodes.system;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;

public class HistoryNode extends ListNode<BNode> {
	private int i = 0;

	public HistoryNode(BBGraph g) {
		super(g);
	}

	public boolean navigateAccrossHistory() {
		return i != get().size();
	}

	public void addToHistory(BNode n) {
		while (get().size() > i) {
			get().removeLast();
		}

		get().add(n);
		i++;
	}

	public boolean backPossible() {
		return i > 0;
	}

	public BNode back() {
		if (!backPossible())
			throw new IllegalStateException();

		return get().get(--i);
	}

	public BNode backTarget() {
		return get().get(i - 1);
	}

	public boolean forwardPossible() {
		return i < get().size() - 1;
	}

	public BNode forward() {
		if (!forwardPossible())
			throw new IllegalStateException();

		return get().get(++i);
	}

	public BNode forwardTarget() {
		return get().get(i + 1);
	}

}
