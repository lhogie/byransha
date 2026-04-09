package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.nodes.system.ChatNode;

public final class FreezingAction extends NodeAction<BNode, BNode> {
	public FreezingAction(BGraph g, BNode node) {
		super(g, node, misc.class);
	}

	public static class misc extends Category {
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
	public ActionResult<BNode, BNode> exec(ChatNode chat) throws Throwable {
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

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}
}