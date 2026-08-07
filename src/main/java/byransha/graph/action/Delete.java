package byransha.graph.action;

import byransha.graph.BNode;
import byransha.graph.Category.node;
import byransha.system.SystemNode;
import byransha.graph.ProcedureAction;

public class Delete extends ProcedureAction<BNode> {

	public Delete(BNode node) {
		super(node, node.class);
	}

	@Override
	public String whatItDoes() {
		return "delete from the graph";
	}

	@Override
	public void impl() {
		inputNode.delete();
	}

	@Override
	public boolean applies() {
		return !(inputNode instanceof SystemNode);
	}
}