package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;

public final class FreezingAction extends NodeAction<BNode, BNode> {
	public FreezingAction(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public boolean wantToBeProposedFor(BNode n) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "loops infinitely";
	}

	@Override
	public ActionResult<BNode, BNode> exec() throws Throwable {
		var r = createResultNode(null, false);

		new Thread(() -> {
			while (true) {
				if (stopRequested) {
					break;
				}
			}
		}).start();

		return r;
	}
}