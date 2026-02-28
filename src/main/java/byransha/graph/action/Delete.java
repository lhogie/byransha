package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;

public class Delete extends ConfirmRequiredNodeAction<BNode, BNode> {

	public Delete(BBGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItDoes() {
		return "delete from the graph";
	}

	@Override
	protected ActionResult execConfirmed(BNode node) {
		node.delete();
		return null;
	}
}