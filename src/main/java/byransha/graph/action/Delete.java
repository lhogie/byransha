package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.system.SystemNode;

public class Delete extends ConfirmRequiredNodeAction<BNode, BNode> {

	public Delete(BGraph g, BNode node) {
		super(g, node);
	}

	@Override
	public String whatItDoes() {
		return "delete from the graph";
	}

	@Override
	protected ActionResult execConfirmed() {
		inputNode.delete();
		return null;
	}

	@Override
	public boolean applies() {
		return !(inputNode instanceof SystemNode);
	}
}