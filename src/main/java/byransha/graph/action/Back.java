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
		return "reset the values";
	}

	@Override
	public ActionResult exec() {
		var h = g.currentUser().history.get();
		var r = h.size() > 1 ? h.get(h.size() - 2) : inputNode;
		return createResultNode(r, true);
	}
}