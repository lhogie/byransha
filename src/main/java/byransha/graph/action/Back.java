package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;

final public class Back extends NodeAction<BNode, BNode> {
	public Back(BGraph g, BNode n) {
		super(g, n);
		execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "back in history";
	}

	@Override
	public ActionResult exec() {
		var h = g.currentUser().history.get();

		if (applies()) {
			h.remove(h.size() - 1);
			var next = h.remove(h.size() - 1);
			return createResultNode(next, true);
		} else {
			return createResultNode(inputNode, true);
		}
	}

	@Override
	public boolean applies() {
		return g.currentUser().history.get().size() > 1;
	}

}