package byransha.graph.action;

import java.lang.annotation.Target;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.LongNode;

public class Jump extends NodeAction<BNode, BNode> {
	final LongNode targetID;
	BNode target;

	public Jump(BGraph g, BNode in) {
		super(g, in);
		targetID = new LongNode(g);
		
		targetID.changeListeners.add(l -> {
			var node = g.indexes.byId.get(targetID.get());
			target = node;
		});
		
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
