package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;

public class Jump extends NodeAction<BNode, BNode> {
	BNode target;

	public Jump(BGraph g, BNode in) {
		super(g, in);
		execStraightAway = true;
		target = g;
	}


	@Override
	public String prettyName() {
		return "jump to node " + target;
	}

	@Override
	public String whatItDoes() {
		return "jumps to another node";
	}

	@Override
	public ActionResult<BNode, BNode> exec() {
		g.currentUser().jumpTo(target);
		return createResultNode(target, true);
	}


	@Override
	public boolean applies() {
		return true;
	}
}
