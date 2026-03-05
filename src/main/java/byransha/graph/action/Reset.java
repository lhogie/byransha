package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.SystemNode;

final public class Reset extends NodeAction<BNode, BNode> {
	public Reset(BGraph g, BNode n) {
		super(g, n);
		execStraightAway = true;
	}

	@Override
	public String whatItDoes() {
		return "reset the values";
	}

	@Override
	public ActionResult exec() {
		inputNode.reset();

		return createResultNode(inputNode, true);
	}
	

	@Override
	public boolean applies() {
		return !(inputNode instanceof SystemNode);
	}
}